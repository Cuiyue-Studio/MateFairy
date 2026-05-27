for f in ~/.gradle/caches/modules-2/files-2.1/com.pico.spatial*/**/*.aar; do
    unzip -l "$f" | grep LocalSpatialNavigator && echo "Found in $f"
done
