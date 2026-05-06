USD, which stands for Universal Scene Description (meaning "general scene description"), is a file format used to describe the 3D scene content in the current working environment.

* From the perspective of scene description, the commonly used format is .usda:
   * The .usda format supports layers, allowing edits to be non-destructively layered during collaborative editing by multiple users.
   * Different developers can add their own unique attributes to facilitate their own development. Taking PICO Spatial as an example, the extended engine can directly use the special attributes you marked in DCC software. For example, when objects containing SDK components specific to PICO Spatial are output from the editor, they are attached to resources as layers. Modifying a resource does not affect any attached components and is considered a non-destructive change. Even if a material is edited again in the editor, as long as the node structure remains unchanged, there will be no issues.
   * In Spatial Editor, scenes are typically saved in the .usda format, placed in the Scenes directory, and automatically identified for packaging along with all referenced assets.
* From the asset perspective, the commonly used formats are USDC and USDZ:
   * USDC is a binary description file that offers the highest flexibility and the best read performance. The file will include a texture folder at the same directory level to store textures. This is required, and the directory structure is relative. This format is typically used when providing assets to the Spatial Editor. Secondary material editing may be performed, and in some cases, you may choose not to output materials.
   * USDZ integrates USDC and texture files, making distribution and direct use convenient. When used with PICO Spatial SDK, it is typically used for direct use with AssetsLoad or for combining multiple assets. It is not appropriate to perform secondary editing on the asset itself.
* The content saved in the file includes the following basic information:
   * Point information for polygons in the scene, matrix transformation information for each vertex relative to the whole, object transformation information, object skeletal information, weight information, and other similar information.
   * The universal UsdPreviewSurface material information is a universal format for PBR material information. You can simply think of this as the values for basic channels such as base color, metallic, roughness, transparency, emissive, and more. It also includes the textures used and the basic sampling method for channels that use textures, such as which RGB channels are used. It also includes the UV channel index used by the material, as well as basic UV operations such as offset, tiling, and rotation. If you export a standard PBR material object from DCC software and select the USDZ format (archive type), you can quickly use it in the asset module of PICO SDK.

## General recommendations
The USD format is highly sensitive to node naming for objects within assets. The name you assign during the production phase in DCC software is the only identifier by which the engine can find nodes. Avoid assigning the same name to nodes during creation, including object naming and naming object mesh data blocks. Unlike other formats, USD typically outputs a structure that matches the project's structure.

* Using Blender as an example, recommended naming conventions for basic objects are as follows:
   * **Object name** is the name of the object in the current scene; there are no special requirements, and it should be simple and easy to understand.
   * **Model data block name** is the node name of the model data component in the USD file format corresponding to the object, and should be kept unique within the scene whenever possible. When the SDK needs to obtain model vertices or model material information, it must locate this node. There are no special requirements for material names.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e74eca23ae614ffe8f5f0bdec4bfd736~tplv-goo7wpa0wc-image.image)
* The USD format has specific requirements for PBR material settings. You must use nodes supported for conversion by UsdPreviewSurface. The recommended texture format is png. Texture names should be as concise and clear as possible, and must not use Chinese.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/de67f77cca6440a88e386d21b0f0bf8c~tplv-goo7wpa0wc-image.image)
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/66813a61e16c4320aad3f16f1b698409~tplv-goo7wpa0wc-image.image)

When exporting USD* assets, the name of the file itself also becomes a node, existing either within the engine or as an entity. Therefore, it is recommended to plan names to improve readability and avoid names that are excessively long or difficult to understand, especially for objects that need to be manipulated during development. For example, using Blender, the output is usually as follows:

* The output file name
   * Node name
      * A **Polygon data node** is an entity in the engine.
         ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6459824d084b46bea2be11257c231849~tplv-goo7wpa0wc-image.image)
   * Results after being imported into Editor. Only output file names have actual sorting significance in asset management within the editor. In engine usage, rendering effect information is provided in ECS mode by node polygon data objects. If the SDK needs to modify materials dynamically, it is necessary to query this layer to obtain material information. When exporting USD, material information and model vertex information are stored on the polygon data node.
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f20c9385edc4454680e8a35a90a9ab63~tplv-goo7wpa0wc-image.image)

## **Naming editor resources**

* The art asset name. Naming example: Earth.usdz
* Art resources are located in the **Assets** folder.
   * Model file
   * Texture files
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5d5e84dcfd9c4d79a65dd82e220f24c4~tplv-goo7wpa0wc-image.image)
* The project is located in the **Scenes**  folder and is a `.usda` file.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4c3c38e519a14872a553dd4112d6cd11~tplv-goo7wpa0wc-image.image)

## Editor import adjustment
### USDZ. file

1. When importing plant files with transparency maps, the files will be opaque if no material is assigned.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/169a077a53834d98b17cdf3cf832b30a~tplv-goo7wpa0wc-image.image)
2. Open the model material file.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f541c1f0351248abab8afcdd98daa82a~tplv-goo7wpa0wc-image.image)
   1. Change Opaque to Transparent in the material.
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6664bc9f87e7477e9c5b5335a68f4807~tplv-goo7wpa0wc-image.image)
   2. Change Opacity Threshold to 0.001
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d8a379938bca4982b76134ee1f422922~tplv-goo7wpa0wc-image.image)
   The model can now display transparency correctly:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d62dc69c30d6418a98eef6c040afbdc6~tplv-goo7wpa0wc-image.image)

### USDC documents
USDC. Effect after importing a file into the editor:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/70bacc154b964bc9a14deff5d42c246f~tplv-goo7wpa0wc-image.image)
A yellow exclamation mark prompt appears with: "This texture has not been marked as a linear texture map." After **Fix now**, it will then display normally.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fa2310ff450742169cc7a39104cde35f~tplv-goo7wpa0wc-image.image)

