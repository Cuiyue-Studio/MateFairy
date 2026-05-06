This article introduces the nodes in Shader Graph.
## What are Shader Graph nodes?
In the node graph of Shader Graph, each node defines a specific function, which can be input, processing, or output. Nodes exchange information with other nodes through input or output ports. You can connect ports of different nodes using edges. Only ports with the same data type can be connected by edges.
Nodes in Shader Graph can be divided into the following categories:
| **Name** | **Function** | **Example** |
| --- | --- | --- |
| **Input node** | Used to provide different types of input data, such as Float, Boolean, Color, Vector, Matrix, and Filename. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3226b18aa08f41608d028ed9937273eb~tplv-goo7wpa0wc-image.image) <br>  |
| **Processing node** | Used to process input data, such as performing mathematical operations, logical evaluations, channel processing, color adjustment, blending and conversion, and defining material properties. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7df7dcf23de04d66956e546793a60538~tplv-goo7wpa0wc-image.image) <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/152012c866894b488300b546beb12ddf~tplv-goo7wpa0wc-image.image) <br>  |
| **Output node** | Used to receive the final result of the node network and output it to the Surface or Geometry Modifier channel according to the result type. <br>  <br> * Surface is used for modifying material surface properties. <br> * Geometry Modifier is used for modifying geometric shapes. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/17e0a25c3c1e4fc79006932727642597~tplv-goo7wpa0wc-image.image) <br>  |
## Node list
### Adjustment
Performs artistic adjustments or corrections in a post-processing style to the input data, which is usually color or numeric values.
| **Node** | **Description** |
| --- | --- |
| [Contrast](/editor/Contrast) | Adjusts the contrast of input values (float or color) using a linear slope multiplier. |
| [HSV Adjust](/editor/HSVAdjust) | Adjusts the HSV of input RGB color using a vector. |
| [HSV To RGB](/editor/HSVToRGB) | Converts color from HSV color space back to RGB color space. |
| [RGB To HSV](/editor/RGBToHSV) | Converts color from RGB color space to HSV color space. |
| [Luminance](/editor/Luminance) | Receives an RGB color as input and outputs a grayscale value representing the brightness of the input color across all channels. |
| [Range](/editor/Range) | Remaps an input value within one range to another range. Also provides options for gamma correction and output limitation. |
| [Remap](/editor/Remap) | Linearly maps input values from one range to another range. |
| [Saturate](/editor/Saturate) | Adjusts color saturation. |
| [Smooth Step](/editor/SmoothStep) | Use the Hermite interpolation method to smoothly remap an Input value within a low-to-high range to an output range of 0 to 1. |
### Channel
Used to access, rearrange, combine, and separate the components (channels) of a vector.
| **Node** | **Description** |
| --- | --- |
| [Convert](/editor/Convert) | Convert the Input data stream from one data type to another. |
| [Combine 2](/editor/Combine2) | Combine the channels of two data streams into a dual-channel output stream of a compatible type. |
| [Extract](/editor/Extract) | Extract the specified channel number from a Color N or Vector N stream. |
| [Combine 3](/editor/Combine3) | Combine the channels from three streams into three channels of a single compatible output stream. |
| [Combine 4](/editor/Combine4) | Combine the channels from four streams into four channels of a single compatible output stream. |
| [Swizzle](/editor/Swizzle) | Perform arbitrary permutations on the channels of the Input stream and return a new data stream of the specified type. |
| [Separate 2](/editor/Separate2) | Output each channel of Vector 2 as a separate Float output. |
| [Separate 3](/editor/Separate3) | Output each channel of Color 3, Vector 3, or Matrix 3 as a separate Float or Float 3 output. |
| [Separate 4](/editor/Separate4) | Output each channel of Color 4, Vector 4, or Matrix 4 as a separate Float or Float 4 output. |
### Composition
Combines multiple data values into a single output. You can use the **Composition** node to combine textures to achieve specific visual effects. For example, the background texture can be displayed only in the transparent areas of the foreground texture.
#### Blend
Performs color blending or compositing of foreground and background input according to different blend modes.
| **Node** | **Description** |
| --- | --- |
| [Burn](/editor/Burn) | A blending operation that uses the background to darken the foreground layer. |
| [Difference](/editor/Difference) | Outputs the distance between the foreground and background values. |
| [Dodge](/editor/Dodge) | A blending operation that brightens the background layer based on the foreground. |
| [Mix](/editor/Mix) | Blend the foreground and background inputs, weighted by the blend value. |
| [Over](/editor/Over) | A merging operation that overlays the foreground onto the background using the foreground's Alpha channel. |
| [Overlay](/editor/Overlay) | A blending operation that performs multiplicative blending on dark areas and screen blending on light areas. |
| [Additive Mix](/editor/AdditiveMix) | A blending operation that adds the foreground value to the background value. |
| [Screen](/editor/Screen) | A blending operation that brightens areas darker than white. |
#### Mask
Extracts, retains, or excludes specific areas of the input based on mask or Alpha overlap relationships.
| **Node** | **Description** |
| --- | --- |
| [Mask](/editor/Mask) | Outputs the region in the background that overlaps with the foreground Alpha. |
| [Matte](/editor/Matte) | A merging operation that overlays a premultiplied foreground layer onto the background. |
| [In](/editor/In) | Outputs the region in the foreground that overlaps with the background Alpha. |
| [Out](/editor/Out) | Outputs the region in the foreground that does not overlap with the background. |
| [Inside](/editor/Inside) | Multiply a mask onto all channels of the Input. |
| [Outside](/editor/Outside) | Multiply the complement of the mask (1 - mask) onto all channels of the Input. |
#### Mode
Performs compositing operations on foreground and background input according to specific compositing modes.
| **Node** | **Description** |
| --- | --- |
| [Disjoint Over](/editor/DisjointOver) | A merge operation that overlays the foreground on top of the background color, assuming that the semi-transparent regions covered by both do not overlap. |
| [Subtractive Mix](/editor/SubtractiveMix) | Subtract the foreground value from the background value. |
#### Premult
The Premult node is used to perform premultiplied or unpremultiplied processing on the input color, that is, multiplying or dividing the RGB channels by the Alpha channel.
| **Node** | **Description** |
| --- | --- |
| [Premult](/editor/Premult) | Multiply the input RGB channels by the input Alpha channel. |
| [Unpremult](/editor/Unpremult) | Divide the input RGB channels by the input Alpha channel. |
### Input
Used to provide different types of input data.
#### Constant
Provides a constant value as input, which can be a numeric value, vector, or color.
| **Node** | **Description** |
| --- | --- |
| [Float](/editor/Float) | Constant floating-point value. |
| [Integer](/editor/Integer) | Constant integer value. |
| [Boolean](/editor/Boolean) | Constant boolean value. |
| [Vector2](/editor/Vector2) | Constant Vector2 containing two floating-point components (x, y). |
| [Vector3](/editor/Vector3) | Constant Vector3 containing three floating-point components (x, y, z). |
| [Vector4](/editor/Vector4) | Constant Vector4 containing four floating-point components (x, y, z, w). |
| [Color3](/editor/Color3) | Constant Color3 vector containing three floating-point components (r, g, b). |
| [Color4](/editor/Color4) | Constant Color4 vector containing four floating-point components (r, g, b, a). |
| [Matrix3x3](/editor/Matrix3x3) | A constant Matrix3x3 (floating-point) value (row-major order). |
| [Matrix4x4](/editor/Matrix4x4) | A constant Matrix4x4 (floating-point) value (row-major order). |
#### Texture
Provides texture data as input.
| **Node** | **Description** |
| --- | --- |
| [Image File](/editor/ImageFile) | Constant path pointing to a local image file. |
| [UV Texture](/editor/UVTexture) | MaterialX version of the USD UV texture reader. |
| [Tiled Image](/editor/TiledImage) | Used to sample data from an image and provides offset and tiling functionality in UV space. |
| [Image](/editor/Image) | Samples data from a single image or a specific layer in a multilayer image. |
| [Mipmap Bias Image](/editor/MipmapBiasImage) | Image node containing parameters for multi-level mipmap texture deviation. |
| [Bound Video Texture](/editor/BoundVideoTexture) | Reads the video texture currently bound to the model and samples it based on the input texture coordinates. |
| [Video Texture LOD](/editor/VideoTextureLOD) | Used to sample pixels from a video file. |
| [Texture Size](/editor/TextureSize) | Retrieve texture size and texel size. |
| [Cube Image](/editor/CubeImage) | Generate a cube texture. |
| [Cube Image LOD](/editor/CubeImageLOD) | Generate and configure a cube texture. Compared to the **Cube Image** node, adds LOD parameters. |
| [Cube Image Gradient](/editor/CubeImageGradiant) | Generate and configure a cube texture. Compared to the **Cube Image** node, adds gradient-related settings. |
#### Data
Provides data input related to the current shading point or bound geometry, such as position, normal, tangent, color, texture coordinates, and other geometric properties.
| **Node** | **Description** |
| --- | --- |
| [Surface Screen Position](/editor/SurfaceScreenPosition) | Screen space coordinates of the data currently being processed. |
| [Bitangent](/editor/Bitangent) | The geometric bitangent vector of the data currently being processed in the specified space coordinate. |
| [Geom Color](/editor/GeomColor) | The color associated with the geometry at the current processing position, usually defined by vertex color. |
| [Geom Propvalue](/editor/t9eu5jwh) | The value of the specified variable geometric property (defined using MaterialX `<geompropdef>`) of the currently bound geometry. |
| [Primvar Reader](/editor/PrimvarReader) | Enables the shading network to use data defined on the geometry. |
| [Normal](/editor/Normal) | The geometric normal associated with the currently processed data, defined in a specific space coordinate. |
| [Position](/editor/Position) | The coordinates associated with the currently processed data, defined in a specific space coordinate. |
| [Tangent](/editor/Tangent) | The geometric tangent vector of the currently processed data in the specified space coordinate. |
| [Texture Coordinates](/editor/TextureCoordinates) | The two-dimensional or three-dimensional texture coordinates of the currently processed data. |
| [Two Sided Sign](/editor/TwoSidedSign) | Returns a sign value (+1 or -1) based on whether the current fragment is on the front or back side of the surface geometry. |
#### Global
Used to access global shading input provided by the system, such as camera, view, transformation matrices, and other rendering context information.
| **Node** | **Description** |
| --- | --- |
| [Camera Index Switch](/editor/CameraIndexSwitch) | Renders different results for each eye in stereoscopic rendering. |
| [Camera Position](/editor/CameraPosition) | The position of the camera in the scene. |
| [Surface Model To View](/editor/SurfaceModeltoView) | The Matrix4x4 (Float) matrix that converts model space to view space coordinate, used for surface shading. |
| [Surface Model To World](/editor/SurfaceModeltoWorld) | The Matrix4x4 (Float) matrix that converts model space to world space coordinate, used for surface shading. |
| [Surface Projection To View](/editor/SurfaceProjectiontoView) | The Matrix4x4 (Float) matrix that converts projection space to view space coordinate, used for surface shading. |
| [Surface View Direction](/editor/SurfaceViewDirection) | Returns the direction vector from the current surface shading point to the view reference point. |
| [View Direction](/editor/ViewDirection) | Returns the direction vector from the specified position to the view reference point, output in the selected space coordinate. |
| [Surface View To Projection](/editor/SurfaceViewtoProjection) | The Matrix4x4 (Float) matrix that converts view space coordinate to projection space, used for surface shading. |
| [Surface World To View](/editor/SurfaceWorldtoView) | The Matrix4x4 (Float) matrix that converts world space coordinate to view space coordinate, used for surface shading. |
| [Vertex Model To View](/editor/VertextModeltoView) | The Matrix4x4 (Float) matrix that converts model space to view space coordinate, used for vertex coordinates. |
| [Vertex Model To World](/editor/VertextModeltoWorld) | The Matrix4x4 (Float) matrix that converts model space to world space coordinate, used for vertex coordinates. |
| [Vertex World To Model](/editor/VertexWorldtoModel) | The Matrix4x4 (Float) matrix that converts world space coordinate to model space, used for vertex coordinates. |
| [Vertex Projection To View](/editor/VertexProjectiontoView) | The Matrix4x4 (Float) matrix that converts projection space to view space coordinate, used for vertex coordinates. |
| [Vertex View To Projection](/editor/VertexViewToProjection) | The Matrix4x4 (Float) matrix that converts view space coordinate to projection space, used for vertex coordinates. |
| [Vertex Normal To World](/editor/uxc15rla) | The Matrix3x3 (Float) matrix that transforms vertex normals from model space to world space coordinate. <br>  |
| [Time](/editor/Time) | Outputs the current time (seconds) of the local environment. |
| [Up Direction](/editor/UpDirection) | The direction of the up vector. |
| [Scene Texel Size](/editor/SceneTexelSize) | Provides the size information of the current scene texture, as well as the normalized size corresponding to a single texel. |
| [Camera Direction](/editor/CameraDirection) | Returns the float3 direction vector of the camera's current orientation. |
| [Camera Up Direction](/editor/CameraUpDirection) | Returns the float3 direction vector of the camera's current up direction. |
| [Object Radius](/editor/ObjectRadius) | Returns the bounding radius of the object in the specified space. |
| [Object Bounds](/editor/fxncdhsm) | Returns the bounding box information of the object in the specified space, including size, minimum coordinates, and maximum coordinates. |
| [Object Position](/editor/z0xkfxod) | Returns the float3 position vector of the object's origin in the specified space. |
| [Object Up Direction](/editor/8muhu5bc) | Returns the float3 direction vector of the object's up direction in the specified space. |
| [Object Forward Direction](/editor/ObjectForwardDirection) | Returns the float3 direction vector of the object's forward direction in the specified space. |
### Logic
Performs conditional judgments and Boolean operations to control the execution flow of the shader.
| **Node** | **Description** |
| --- | --- |
| [If Equal](/editor/IfEqual) | Outputs different results based on whether **Value1** is equal to **Value2**: if they are equal, outputs **True Result**; otherwise, outputs **False Result**. |
| [If Greater](/editor/If_Greater) | Outputs different results based on whether **Value1** is greater than **Value2**: if **Value1** > **Value2**, outputs **True Result**; otherwise, outputs **False Result**. |
| [If Greater Or Equal](/editor/IfGreaterOrEqual) | Outputs different results based on whether **Value1** is greater than or equal to **Value2**: if **Value1** >= **Value2**, outputs **True Result**; otherwise, outputs **False Result**. |
| [Switch](/editor/Switch) | Selects and outputs a value from 10 input streams based on the value of the selector input switch. |
| [And](/editor/And) | Performs a logical AND operation on the two boolean values **In 1** and **In 2**. |
| [Or](/editor/Or) | Performs a logical OR operation on the two boolean values **In 1** and **In 2**. |
| [Xor](/editor/Xor) | Performs a logical XOR operation on the two boolean values **In 1** and **In 2**. |
| [Not](/editor/Not) | Returns the result of the logical NOT (!) operation on the input. |
### Math
Performs mathematical operations and transformations on numeric values.
#### Basic
Provides basic mathematical operations, such as addition, subtraction, multiplication, division, and more.
| **Node** | **Description** |
| --- | --- |
| [Add](/editor/Add) | Adds two values. |
| [Divide](/editor/Divide) | Divides two values. Dividing two matrices results in the product of the inverse matrices of <a i=1>in1</a> and <a i=2>in2</a>. |
| [Multiply](/editor/Multiply) | Multiplies two values. |
| [Power](/editor/Power) | Calculates the specified power of a value. |
| [Safe Power](/editor/SafePower) | Calculates the specified power of a value and assigns the sign of the base to the output. |
| [Sqrt](/editor/Sqrt) | Calculates the square root of a value. |
| [Subtract](/editor/Subtract) | Subtracts two values. |
#### Advanced
Provides more complex mathematical operations and transformations, such as exponential functions, logarithmic functions, partial derivatives, and more.
| **Node** | **Description** |
| --- | --- |
| [Absval](/editor/Absval) | Outputs the absolute value of each input channel. |
| [Exponential 2](/editor/Exponential2) | 2 to the power of X. |
| [Exponential 10](/editor/Exponential_10) | 10 to the power of X. |
| [Exp](/editor/Exp) | Outputs e raised to the power of the input value. |
| [Modulo](/editor/Modulo) | Divides **In 1** by **In 2**, subtracts the integer part, and outputs the remaining fractional part. |
| [Log](/editor/Log) | The natural logarithm of the input. |
| [Log 2](/editor/Log2) | The base-2 logarithm of the input. |
| [Log 10](/editor/Log10) | The base-10 logarithm of the input. |
| [Normal Map Decode](/editor/NormalMapDecode) | By applying the formula `2x - 1`, the range of normal values can be remapped from `[0, 1]` to `[-1, 1]`. |
| [Distance](/editor/Distance) | Returns the distance between X and Y. |
| [Distance Square](/editor/DistanceSquare) | Returns the squared distance between X and Y. |
| [DDX](/editor/DDX) | Returns the partial derivative of the input value in the screen space X direction. |
| [DDY](/editor/DDY) | Returns the partial derivative of the input value in the screen space Y direction. |
#### Range
Sets the range of values.
| **Node** | **Description** |
| --- | --- |
| [Fractional](/editor/Fractional) | Returns the fractional part of a floating-point number. |
| [Clamp](/editor/Clamp) | Clamps the input per channel between **Low** and **High**. |
| [Max](/editor/en_Max) | Output the maximum value between **In 1** and **In 2**. |
| [Min](/editor/Min) | Output the minimum value between **In 1** and **In 2**. |
| [One Minus](/editor/OneMinus) | Subtract the Input value from 1. |
#### Round
Performs rounding, ceiling, or floor operations on values.
| **Node** | **Description** |
| --- | --- |
| [Floor](/editor/Floor) | Output the nearest integer value per channel that is less than or equal to the incoming value. |
| [Ceil](/editor/Ceil) | Output the nearest integer value per channel that is greater than or equal to the incoming value. |
| [Step](/editor/Step) | If **In** < **Edge**, return 0.0; otherwise, return 1.0. |
| [Round](/editor/Round) | Round per channel to the nearest integer value. |
| [Sign](/editor/Sign) | The sign of the Input value for each channel: -1 for negative, +1 for positive, 0 for zero. |
#### Geometry
Handles geometric and spatial data operations, such as vector operations, normal processing, direction transformations, and texture coordinate transformations.
| **Node** | **Description** |
| --- | --- |
| [Cross Product](/editor/CrossProduct) | Calculate the cross product of two input vectors. |
| [Dot Product](/editor/DotProduct) | Output the dot product of two vectors. |
| [Magnitude](/editor/Magnitude) | Output the floating-point magnitude of the vector. |
| [Reflect](/editor/Reflect) | Calculate the reflection result of one vector about another vector. |
| [Refract](/editor/Refract) | Calculate the refraction result of the Input vector based on the given surface normal and refractive index (eta). |
| [Normalize](/editor/Normalize) | Output the normalized vector. |
| [Normal Map](/editor/NormalMap) | Convert the normal vector from object space or tangent space to world space. |
| [Rotate 2D](/editor/Rotate2D) | Rotate a 2D vector about the origin in two-dimensional space. |
| [Rotate 3D](/editor/Rotate3D) | Rotate a 3D vector around the specified unit axis vector. |
| [Place 2D](/editor/Place2D) | Transform UV texture coordinates used for 2D texture placement. |
#### Matrix
Performs matrix operations, such as matrix multiplication, transposition, inversion, and more.
| **Node** | **Description** |
| --- | --- |
| [Inverse Matrix](/editor/InverseMatrix) | Output the inverse matrix of the matrix. |
| [Determinant](/editor/Determinant) | Output the floating-point determinant of the matrix. |
| [Transpose](/editor/Transpose) | Output the transpose matrix of the matrix. |
#### Procedural
##### 2D Procedural
Generates 2D noise for the material.
| **Node** | **Description** |
| --- | --- |
| [Cell Noise 2D](/editor/CellNoise2D) | 2D cellular noise generator. |
| [Noise 2D](/editor/Noise2D) | 2D Perlin noise generator. |
| [Ramp 4 Corners](/editor/Ramp4Corners) | Four-point linear value gradient generator. |
| [Ramp Horizontal](/editor/RampHorizontal) | Left-to-right linear value gradient generator. |
| [Ramp Vertical](/editor/RampVertical) | Top-to-bottom linear value gradient generator. |
| [Split Horizontal](/editor/SplitHorizontal) | Split the mask from left to right at the specified U value. |
| [Split Vertical](/editor/SplitVertical) | Split the mask from top to bottom at the specified V value. |
| [Worley Noise 2D](/editor/WorleyNoise2D) | 2D Worley noise generator. |
##### 3D Procedural
Generates 3D noise for the material.
| **Node** | **Description** |
| --- | --- |
| [Cellular Noise 3D](/editor/CellularNoise3D) | 3D cellular noise generator. |
| [Fractal Noise 3D](/editor/FractalNoise3D) | Generate a 3D fractal noise centered at 0 by stacking multiple 3D Perlin noise layers (octaves) with different frequencies and amplitudes. |
| [Noise 3D](/editor/Noise3D) | 3D Perlin noise generator. |
| [Worley Noise 3D](/editor/WorleyNoise3D) | 3D Worley noise generator. |
#### Transforms
Applies transformations to the material's texture, color, and other properties.
| **Node** | **Description** |
| --- | --- |
| [Transform 2D](/editor/Transform2D) | Node for applying an affine transformation to 2D Input. |
| [Transform Matrix](/editor/TransformMatrix) | Transform a vector by matrix. |
| [Transform Normal](/editor/TransformNormal) | Convert normals from one space to another. |
| [Transform Point](/editor/TransformPoint) | Transform coordinates from one space to another. |
| [Transform Vector](/editor/TransformVector) | Transform a 3D vector from one space to another space. |
#### Trigometry
Performs trigonometric operations in the material.
| **Node** | **Description** |
| --- | --- |
| [Cos](/editor/Cos) | The cosine value of the input (in radians). |
| [Sin](/editor/Sin) | The sine value of the input (in radians). |
| [Tan](/editor/Tan) | The tangent value of the input (in radians). |
| [Acos](/editor/Acos) | The arccosine value of the input (in radians). |
| [Asin](/editor/Asin) | The arcsine value of the input (in radians). |
| [Atan2](/editor/Atan2) | The arctangent value (in radians) of **In Y** / **In X**. |
| [PI](/editor/PI) | Returns the value of PI (π). |
### Surface
Defines the final physical properties of the material surface and outputs them to the engine's rendering pipeline.
| **Node** | **Description** |
| --- | --- |
| [Preview Surface](/editor/PreviewSurface) | MaterialX version of USD preview surface. |
| [PBR Surface](/editor/PBRSurface) | Surface shader for physically based rendering (PBR) materials. |
| [Unlit Surface](/editor/UnlitSurface) | Surface shader for unlit materials. |
| [Occlusion Surface](/editor/OcclusionSurface) | Surface shader that defines properties for occlusion materials that do not receive dynamic lighting. |
### Vertex
Adjusts the position of model vertices.
| **Node** | **Description** |
| --- | --- |
| [Geometry Modifier](/editor/GeometryModifier) | Modify the vertex properties of geometry based on input parameters, executed once for each vertex. |
| [Vertex Index](/editor/VertexIndex) | Returns the index of the current vertex in the mesh. |
### Other
| **Node** | **Description** |
| --- | --- |
| [Node Graph](/editor/NodeGraph) | Node that can contain shader nodes and other node graphs. |
| [Dot](/editor/Dot) | Relay node used for visual guidance of connections in the node graph. |
| [Named Dot](/editor/NamedDot) | Value relay node. Functions similarly to a global variable, used to pass data within the Node Graph. |
| [Debug Value](/editor/DebugValue) | Render the input value to the surface for display. |
| [Sticky Note](/editor/StickyNote) | Sticky note, used to add comments to the node graph. |
| [Environment Radiance](/editor/EnvironmentalRadiance) | Based on real-world environmental information and an IBL map (which can be provided by the developer or use the default map), returns the diffuse radiance and specular radiance values of the environment. |
| [Instance Custom Data](/editor/InstanceCustomData) | Returns the floating-point value stored in the instance custom data. |
| [Fresnel Effect](/editor/FresnelEffect) | Calculates the Fresnel effect based on the direction of the surface normal and the view direction. |
