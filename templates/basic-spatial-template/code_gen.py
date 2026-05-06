
import sys
import re
import os
import json

DEPEND_RGX = r"\"(.*)\""
TAIL_START = "##tail_start##"
TAIL_END = "##tail_end##"

class BuildGradle:
    applicationId = ""
    implementation = []
    implementationPlatform = []
    implementationDebug = []
    tail = ""
    ktFile = {}

    def addImplementation(self, impl: str):
        if (len(impl) > 0):
            self.implementation.append(impl)

    def addImplementationPlatform(self, impl: str):
        if (len(impl) > 0):
            self.implementationPlatform.append(impl)

    def addImplementationDebug(self, impl: str):
        if (len(impl) > 0):
            self.implementationDebug.append(impl)
    def addKtFileConfig(self, name: str, path: str):
        self.ktFile[name] = path
    def packageName(self)->str:
        return self.applicationId.replace("\"", "")

def getAppBuildGradleInfoFromProject(src: str)-> BuildGradle:
    gradlePath = src + "/app/build.gradle.kts"
    buildGradle = BuildGradle()
    onTail = False
    tail = ""
    for line in open(gradlePath):
        if TAIL_START in line:
            onTail = True
            continue
        if TAIL_END in line:
            onTail = False
            continue
        if onTail:
            tail = tail + line + "\n"
            continue
        if "applicationId" in line:
            buildGradle.applicationId = line.replace("applicationId", "").replace("=", "").strip()
        elif "implementation(platform" in line:
            buildGradle.addImplementationPlatform(re.search(DEPEND_RGX, line).group().replace("\"", ""))
        elif "debugImplementation" in line:
            buildGradle.addImplementationDebug(re.search(DEPEND_RGX, line).group().replace("\"", ""))
        elif "implementation" in line:
            buildGradle.addImplementation(re.search(DEPEND_RGX, line).group().replace("\"", ""))
    buildGradle.tail = tail
    return buildGradle

def writeAppGradleJsonToFile(gradleInfo: BuildGradle, dest: str):
    content = {
        "dependencies": {
            "platform": gradleInfo.implementationPlatform,
            "implementation": gradleInfo.implementation,
            "debugImplementation": gradleInfo.implementationDebug
        },
        "tail": gradleInfo.tail,
        "srcConfig": gradleInfo.ktFile
    }
    with open(dest + "/appgradle.json", 'w+') as f:
        f.write(json.dumps(content))

def findAllFile(base: str):
    print(base)
    for root, ds, fs in os.walk(base):
        print(root)
        yield from fs

def applicationIdToPath(applicationId: str)->str:
    return applicationId.replace(".", "/").replace("\"", "")

def transKtFileToFtlFile(root: str, src: str, dest: str, gradleInfo: BuildGradle):
    pkgName = gradleInfo.packageName()
    with open(dest + "/" + src + ".ftl", "w+") as f: 
        for line in open(root + "/" + src):
            if "package" in line:
                f.write(line.replace(pkgName, "${package_name}"))
                f.write("\n")
            else:
                f.write(line)

def transKtToFtl(src: str, dest: str, gradleInfo: BuildGradle):
    rootDir = src + "/app/src/main/java/" + applicationIdToPath(gradleInfo.applicationId)
    for root, ds, fs in os.walk(rootDir):
        for f in fs:
            if root == rootDir:
                gradleInfo.addKtFileConfig(f, "")
            else:
                gradleInfo.addKtFileConfig(f, root.replace(rootDir + "/", ""))
            transKtFileToFtlFile(root, f, dest, gradleInfo)

def writeSourceFileToDest(src: str, dest: str):
    with open(dest, "w+") as f:
        for line in open(src):
            f.write(line)

def transOtherFilesToFtl(src: str, dest: str, gradleInfo: BuildGradle):
    srcTheme = src + "/app/src/main/res/values/themes.xml"
    destTheme = dest + "/themes.xml.ftl"
    writeSourceFileToDest(srcTheme, destTheme)
    srcAndroidManifest = src + "/app/src/main/AndroidManifest.xml"
    destAndroidManifest = dest + "/AndroidManifest.xml.ftl"
    writeSourceFileToDest(srcAndroidManifest, destAndroidManifest)

def genTemp(src: str, dest: str):
    os.makedirs(dest, exist_ok=True)
    gradleInfo = getAppBuildGradleInfoFromProject(src)
    transKtToFtl(src, dest, gradleInfo)
    transOtherFilesToFtl(src, dest, gradleInfo)
    writeAppGradleJsonToFile(gradleInfo, dest)


# Generate SpatialPlugin template files from project
#python3 code_gen.py -c project -src <project_dir> -desc <output_dir>

def getArgs(key, argsMap, errMsg):
    if key in argsMap:
        return argsMap[key]
    if len(errMsg) > 0:
        print(errMsg)
        sys.exit()
    return ""

def getOrDefault(key, argsMap, default):
    if key in argsMap:
        return argsMap[key]
    return default

args = sys.argv
argsLen = len(args)
argsMap = {}

if argsLen <= 1:
    print("Arguments are empty")
    sys.exit()
i = 1
while i < argsLen:
    argsMap[args[i]] = args[i + 1]
    i = i + 2

cmd = getArgs('-c', argsMap, "Missing instruction: -c project")
if cmd == "project":
    src = getArgs('-src', argsMap, "Missing source directory")
    out = getOrDefault("-desc", argsMap, "./desc")
    genTemp(src, out)


