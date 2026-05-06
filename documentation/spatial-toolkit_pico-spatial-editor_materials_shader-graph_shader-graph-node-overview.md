This article introduces the nodes in Shader Graph.
## About Shader Graph nodes
In the node graph of Shader Graph, each node defines a specific function, which can be input, processing, or output. Nodes exchange information with other nodes through input or output ports. You can connect the ports of different nodes using edges. Only ports with the same data type can be connected by edges.
Nodes in Shader Graph can be divided into the following categories:
| **Name** | **Function** | **Example** |
| --- | --- | --- |
| **Input node** | Used to provide different types of input data, such as Float, Boolean, Color, Vector, Matrix, and Filename. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3226b18aa08f41608d028ed9937273eb~tplv-goo7wpa0wc-image.image) <br>  |
| **Processing node** | Used to process input data, such as performing mathematical operations, logical judgments, channel processing, color adjustment, blending and conversion, and defining material properties. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7df7dcf23de04d66956e546793a60538~tplv-goo7wpa0wc-image.image) <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/152012c866894b488300b546beb12ddf~tplv-goo7wpa0wc-image.image) <br>  |
| **Output node** | Used to receive the final result of the node network and output it to the Surface or Geometry Modifier channel according to the result type. <br>  <br> * Surface is used for modifying material surface properties. <br> * Geometry Modifier is used for modifying geometric shapes. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/17e0a25c3c1e4fc79006932727642597~tplv-goo7wpa0wc-image.image) <br>  |
## Node list
### Adjustment
Performs artistic adjustments or corrections in a post-processing style to the input data, which is usually color or numerical values.
| **Node** | **Description** |
| --- | --- |
| [Contrast](/spatial-editor-shader-graph-Contrast) | Adjusts the contrast of input values (float or color) by applying a linear slope multiplier. |
| [HSV Adjust](/spatial-editor-shader-graph-HSVAdjust) | Adjusts the HSV of input RGB color using a vector. |
| [HSV To RGB](/spatial-editor-shader-graph-HSVToRGB) | Converts color from HSV color space back to RGB color space. |
| [RGB To HSV](/spatial-editor-shader-graph-RGBToHSV) | Converts color from RGB color space to HSV color space. |
| [Luminance](/spatial-editor-shader-graph-Luminance) | Receives an RGB color as input and outputs a grayscale value, with the luminance information replicated across all color channels. |
| [Range](/spatial-editor-shader-graph-Range) | Remaps an input value from one range to another. Also provides options for gamma correction and limiting output. |
| [Remap](/spatial-editor-shader-graph-Remap) | Linearly maps input values from one range to another. |
| [Saturate](/spatial-editor-shader-graph-Saturate) | Adjusts color saturation. |
| [Smooth Step](/spatial-editor-shader-graph-SmoothStep) | Use the Hermite interpolation method to smoothly remap an Input value in a low-to-high range to an output range from 0 to 1. |
### Channel
Used to access, rearrange, combine, and separate the components (channels) of a vector.
| **Node** | **Description** |
| --- | --- |
| [Convert](/spatial-editor-shader-graph-Convert) | Convert the Input data stream from one data type to another. |
| [Combine 2](/spatial-editor-shader-graph-Combine2) | Combine the channels of two data streams into a dual-channel output stream of a compatible type. |
| [Extract](/spatial-editor-shader-graph-Extract) | Extract the specified channel number from a Color N or Vector N stream. |
| [Combine 3](/spatial-editor-shader-graph-Combine3) | Combine the channels from three streams into three channels of a single compatible output stream. |
| [Combine 4](/spatial-editor-shader-graph-Combine4) | Combine the channels from four streams into four channels of a single compatible output stream. |
| [Swizzle](/spatial-editor-shader-graph-Swizzle) | Perform arbitrary rearrangement of the Input stream's channels and return a new data stream of the specified type. |
| [Separate 2](/spatial-editor-shader-graph-Separate2) | Output each channel of Vector 2 separately as an individual Float output. |
| [Separate 3](/spatial-editor-shader-graph-Separate3) | Output each channel of Color 3, Vector 3, or Matrix 3 separately as an individual Float or Float 3 output. |
| [Separate 4](/spatial-editor-shader-graph-Separate4) | Output each channel of Color 4, Vector 4, or Matrix 4 separately as an individual Float or Float 4 output. |
### Composition
Combines multiple data values into a single output. You can use the **Composition** node to combine textures to achieve specific visual effects. For example, the background texture can be displayed only in areas where the foreground texture is transparent.
#### Blend
Mixes or composites the color of foreground and background input according to different blend modes.
| **Node** | **Description** |
| --- | --- |
| [Burn](/spatial-editor-shader-graph-Burn) | A blending operation that uses the background to darken the foreground layer. |
| [Difference](/spatial-editor-shader-graph-Difference) | Outputs the distance between the foreground and background values. |
| [Dodge](/spatial-editor-shader-graph-Dodge) | A blending operation that brightens the background layer based on the foreground. |
| [Mix](/spatial-editor-shader-graph-Mix) | Mixes the foreground and background inputs, weighted by the mix value. |
| [Over](/spatial-editor-shader-graph-Over) | A compositing operation that overlays the foreground onto the background using the foreground's Alpha channel. |
| [Overlay](/spatial-editor-shader-graph-Overlay) | A blending operation that multiplies the dark areas and applies screen blending to the light areas. |
| [Additive Mix](/spatial-editor-shader-graph-AdditiveMix) | A blending operation that adds the foreground value to the background value. |
| [Screen](/spatial-editor-shader-graph-Screen) | A blending operation that brightens areas darker than white. |
#### Mask
Extracts, retains, or excludes specific areas of the input based on mask or Alpha overlap relationships.
| **Node** | **Description** |
| --- | --- |
| [Mask](/spatial-editor-shader-graph-Mask) | Outputs the area in the background that overlaps with the foreground Alpha. |
| [Matte](/spatial-editor-shader-graph-Matte) | A compositing operation that overlays a premultiplied foreground layer onto the background. |
| [In](/spatial-editor-shader-graph-In) | Outputs the area in the foreground that overlaps with the background Alpha. |
| [Out](/spatial-editor-shader-graph-Out) | Outputs the area in the foreground that does not overlap with the background. |
| [Inside](/spatial-editor-shader-graph-Inside) | Multiply a mask onto all channels of the Input. |
| [Outside](/spatial-editor-shader-graph-Outside) | Multiply the complement of the mask (1 - mask) onto all channels of the Input. |
#### Mode
Performs compositing operations on foreground and background input according to specific compositing modes.
| **Node** | **Description** |
| --- | --- |
| [Disjoint Over](/spatial-editor-shader-graph-DisjointOver) | A merge operation that overlays the foreground on the background color, assuming that the semi-transparent areas covered by both do not overlap. |
| [Subtractive Mix](/spatial-editor-shader-graph-SubtractiveMix) | Subtracts the foreground value from the background value (that is, background value minus foreground value). |
#### Premult
The Premult node is used to perform premultiplied or unpremultiplied processing on input color, that is, multiplying or dividing the RGB channels by the Alpha channel.
| **Node** | **Description** |
| --- | --- |
| [Premult](/spatial-editor-shader-graph-Premult) | Multiplies the Input RGB channels by the Input Alpha channel. |
| [Unpremult](/spatial-editor-shader-graph-Unpremult) | Divides the Input RGB channels by the Input Alpha channel. |
### Input
Used to provide different types of input data.
#### Constant
Provides a constant value as input, which can be a numerical value, vector, or color.
| **Node** | **Description** |
| --- | --- |
| [Float](/spatial-editor-shader-graph-Float) | Constant floating-point value. |
| [Integer](/spatial-editor-shader-graph-Integer) | Constant integer value. |
| [Boolean](/spatial-editor-shader-graph-Boolean) | Constant boolean value. |
| [Vector2](/spatial-editor-shader-graph-Vector2) | Constant Vector2 containing two floating-point components (x, y). |
| [Vector3](/spatial-editor-shader-graph-Vector3) | Constant Vector3 containing three floating-point components (x, y, z). |
| [Vector4](/spatial-editor-shader-graph-Vector4) | Constant Vector4 containing four floating-point components (x, y, z, w). |
| [Color3](/spatial-editor-shader-graph-Color3) | Constant Color3 vector containing three floating-point components (r, g, b). |
| [Color4](/spatial-editor-shader-graph-Color4) | Constant Color4 vector containing four floating-point components (r, g, b, a). |
| [Matrix3x3](/spatial-editor-shader-graph-Matrix3x3) | A constant Matrix3x3 (floating-point) value (row-major order). |
| [Matrix4x4](/spatial-editor-shader-graph-Matrix4x4) | A constant Matrix4x4 (floating-point) value (row-major order). |
#### Texture
Provides texture data as input.
| **Node** | **Description** |
| --- | --- |
| [Image File](/spatial-editor-shader-graph-ImageFile) | Constant path pointing to a local image file. |
| [UV Texture](/spatial-editor-shader-graph-UVTexture) | A MaterialX implementation of the USD UV texture reader node. |
| [Tiled Image](/spatial-editor-shader-graph-TiledImage) | Samples data from an image and provides offset and tiling functionality in UV space. |
| [Image](/spatial-editor-shader-graph-Image) | Samples data from a single image or a specific layer in a multilayer image. |
| [Mipmap Bias Image](/spatial-editor-shader-graph-MipmapBiasImage) | Image node containing parameters for multiple levels of mipmap bias. |
| [Bound Video Texture](/spatial-editor-shader-graph-BoundVideoTexture) | Reads the video texture bound to the current model and samples it based on the Input texture coordinates. |
| [Video Texture LOD](/spatial-editor-shader-graph-VideoTextureLOD) | Samples pixels from a video file. |
| [Texture Size](/spatial-editor-shader-graph-TextureSize) | Retrieves the texture size and texel size. |
| [Cube Image](/spatial-editor-shader-graph-CubeImage) | Generates a cube texture. |
| [Cube Image LOD](/spatial-editor-shader-graph-CubeImageLOD) | Generates and configures a cube texture. Adds LOD parameters compared to the **Cube Image** node. |
| [Cube Image Gradient](/spatial-editor-shader-graph-CubeImageGradiant) | Generates and configures a cube texture. Adds gradient-related settings compared to the **Cube Image** node. |
| [Image 2D](/spatial-editor-shader-graph-noth0ily) | Generates a 2D texture from an image file. |
| [Image 2D LOD](/spatial-editor-shader-graph-s2kla19w) | Creates a 2D texture from an image file and supports explicitly specifying the LOD level used for sampling. |
#### Data
Provides input data related to the current shading point or bound geometry, such as position, normal, tangent, color, texture coordinates, and other geometric properties.
| **Node** | **Description** |
| --- | --- |
| [Surface Screen Position](/spatial-editor-shader-graph-SurfaceScreenPosition) | Screen space coordinates of the data currently being processed. |
| [Bitangent](/spatial-editor-shader-graph-Bitangent) | The geometric bitangent vector of the data currently being processed in the specified space coordinate. |
| [Geom Color](/spatial-editor-shader-graph-GeomColor) | The color associated with the geometry at the position currently being processed, usually defined by vertex color. |
| [Geom Propvalue](/spatial-editor-shader-graph-t9eu5jwh) | The value of the specified variable geometric property (defined using MaterialX) of the currently bound geometry. |
| [Primvar Reader](/spatial-editor-shader-graph-PrimvarReader) | Enables the shading network to use data defined on the geometry. |
| [Normal](/spatial-editor-shader-graph-Normal) | The geometric normal associated with the currently processed data, defined in a specific space coordinate. |
| [Position](/spatial-editor-shader-graph-Position) | The position associated with the currently processed data, defined in a specific space coordinate. |
| [Tangent](/spatial-editor-shader-graph-Tangent) | The geometric tangent vector of the currently processed data in the specified space coordinate. |
| [Texture Coordinates](/spatial-editor-shader-graph-TextureCoordinates) | The two-dimensional or three-dimensional texture coordinates of the currently processed data. |
| [Two Sided Sign](/spatial-editor-shader-graph-TwoSidedSign) | Returns a sign value (+1 or -1) based on whether the current fragment is on the front or back side of the surface geometry. |
#### Global
Used to access global shading input provided by the system, such as camera, view, transformation matrices, and other rendering context information.
| **Node** | **Description** |
| --- | --- |
| [Camera Index Switch](/spatial-editor-shader-graph-CameraIndexSwitch) | Renders different results for each eye in stereoscopic rendering. |
| [Camera Position](/spatial-editor-shader-graph-CameraPosition) | The position of the camera in the scene. |
| [Surface Model To View](/spatial-editor-shader-graph-SurfaceModeltoView) | A Matrix4x4 (Float) matrix that converts model space to view space for surface shading. |
| [Surface Model To World](/spatial-editor-shader-graph-SurfaceModeltoWorld) | A Matrix4x4 (Float) matrix that converts model space to world space for surface shading. |
| [Surface Projection To View](/spatial-editor-shader-graph-SurfaceProjectiontoView) | A Matrix4x4 (Float) matrix that converts projection space to view space for surface shading. |
| [Surface View Direction](/spatial-editor-shader-graph-SurfaceViewDirection) | Returns the direction vector from the current surface shading point to the view reference point. |
| [View Direction](/spatial-editor-shader-graph-ViewDirection) | Returns the direction vector from the specified position to the view reference point, output in the selected space coordinate. |
| [Surface View To Projection](/spatial-editor-shader-graph-SurfaceViewtoProjection) | A Matrix4x4 (Float) matrix that converts view space to projection space for surface shading. |
| [Surface World To View](/spatial-editor-shader-graph-SurfaceWorldtoView) | A Matrix4x4 (Float) matrix that converts world space to view space for surface shading. |
| [Vertex Model To View](/spatial-editor-shader-graph-VertextModeltoView) | A Matrix4x4 (Float) matrix that converts model space to view space for vertex shading. |
| [Vertex Model To World](/spatial-editor-shader-graph-VertextModeltoWorld) | A Matrix4x4 (Float) matrix that converts model space to world space for vertex shading. |
| [Vertex World To Model](/spatial-editor-shader-graph-VertexWorldtoModel) | A Matrix4x4 (Float) matrix that converts world space to model space for vertex shading. |
| [Vertex Projection To View](/spatial-editor-shader-graph-VertexProjectiontoView) | A Matrix4x4 (Float) matrix that converts projection space to view space for vertex shading. |
| [Vertex View To Projection](/spatial-editor-shader-graph-VertexViewToProjection) | A Matrix4x4 (Float) matrix that converts view space to projection space for vertex shading. |
| [Vertex Normal To World](/spatial-editor-shader-graph-uxc15rla) | A Matrix3x3 (Float) matrix that transforms vertex normals from model space to world space. <br>  |
| [Time](/spatial-editor-shader-graph-Time) | Outputs the current time (in seconds) of the local environment. |
| [Up Direction](/spatial-editor-shader-graph-UpDirection) | The direction of the up vector. |
| [Scene Texel Size](/spatial-editor-shader-graph-SceneTexelSize) | Provides the size information of the current scene texture, as well as the normalized size corresponding to a single texel. |
| [Camera Direction](/spatial-editor-shader-graph-CameraDirection) | Returns the camera's current orientation as a float3 direction vector. |
| [Camera Up Direction](/spatial-editor-shader-graph-CameraUpDirection) | Returns the camera's current up direction as a float3 direction vector. |
| [Object Radius](/spatial-editor-shader-graph-ObjectRadius) | Returns the object's bounding radius in the specified space. |
| [Object Bounds](/spatial-editor-shader-graph-fxncdhsm) | Returns the object's bounding box information in the specified space, including size, minimum coordinates, and maximum coordinates. |
| [Object Position](/spatial-editor-shader-graph-z0xkfxod) | Returns the object's origin position as a float3 position vector in the specified space. |
| [Object Up Direction](/spatial-editor-shader-graph-8muhu5bc) | Returns the object's up direction as a float3 direction vector in the specified space. |
| [Object Forward Direction](/spatial-editor-shader-graph-ObjectForwardDirection) | Returns the object's forward direction as a float3 direction vector in the specified space. |
### Logic
Performs conditional judgments and Boolean operations to control the execution flow of the shader.
| **Node** | **Description** |
| --- | --- |
| [If Equal](/spatial-editor-shader-graph-IfEqual) | Outputs different results depending on whether **Value1** is equal to **Value2**: if they are equal, outputs **True Result**; otherwise, outputs **False Result**. |
| [If Greater](/spatial-editor-shader-graph-If_Greater) | Outputs different results depending on whether **Value1** is greater than **Value2**: if **Value1** > **Value2**, outputs **True Result**; otherwise, outputs **False Result**. |
| [If Greater Or Equal](/spatial-editor-shader-graph-IfGreaterOrEqual) | Outputs different results depending on whether **Value1** is greater than or equal to **Value2**: if **Value1** >= **Value2**, outputs **True Result**; otherwise, outputs **False Result**. |
| [Switch](/spatial-editor-shader-graph-Switch) | Selects and outputs one value from 10 input streams based on the value of the selector input switch. |
| [And](/spatial-editor-shader-graph-And) | Logical AND operation on the two boolean values **In 1** and **In 2**. |
| [Or](/spatial-editor-shader-graph-Or) | Logical OR operation on the two boolean values **In 1** and **In 2**. |
| [Xor](/spatial-editor-shader-graph-Xor) | Logical XOR operation on the two boolean values **In 1** and **In 2**. |
| [Not](/spatial-editor-shader-graph-Not) | Returns the logical NOT (!) operation result of the input. |
### Math
Performs mathematical operations and transformations on numerical values.
#### Basic
Provides basic mathematical operations, such as addition, subtraction, multiplication, division, and more.
| **Node** | **Description** |
| --- | --- |
| [Add](/spatial-editor-shader-graph-Add) | Adds two values. |
| [Divide](/spatial-editor-shader-graph-Divide) | Divides two values. Dividing two matrices is the product of in1 and the inverse of in2. |
| [Multiply](/spatial-editor-shader-graph-Multiply) | Multiplies two values. |
| [Power](/spatial-editor-shader-graph-Power) | Calculates the specified power of a value. |
| [Safe Power](/spatial-editor-shader-graph-SafePower) | Calculates the specified power of a value and assigns the sign of the base to the output. |
| [Sqrt](/spatial-editor-shader-graph-Sqrt) | Calculates the square root of a value. |
| [Subtract](/spatial-editor-shader-graph-Subtract) | Subtracts two values. |
#### Advanced
Provides more complex mathematical operations and transformations, such as exponential functions, logarithmic functions, partial derivatives, and more.
| **Node** | **Description** |
| --- | --- |
| [Absval](/spatial-editor-shader-graph-Absval) | Outputs the absolute value of each input channel. |
| [Exponential 2](/spatial-editor-shader-graph-Exponential2) | 2 raised to the power of X. |
| [Exponential 10](/spatial-editor-shader-graph-Exponential_10) | 10 raised to the power of X. |
| [Exp](/spatial-editor-shader-graph-Exp) | Outputs e raised to the power of the input value. |
| [Modulo](/spatial-editor-shader-graph-Modulo) | Divides **In 1** by **In 2**, subtracts the integer part, and outputs the remaining fractional part. |
| [Log](/spatial-editor-shader-graph-Log) | Natural logarithm. |
| [Log 2](/spatial-editor-shader-graph-Log2) | Base-2 logarithm. |
| [Log 10](/spatial-editor-shader-graph-Log10) | Base-10 logarithm. |
| [Normal Map Decode](/spatial-editor-shader-graph-NormalMapDecode) | Applies the formula `2x - 1` to remap the normal value range from `[0, 1]` to `[-1, 1]`. |
| [Distance](/spatial-editor-shader-graph-Distance) | Returns the distance between X and Y. |
| [Distance Square](/spatial-editor-shader-graph-DistanceSquare) | Returns the square of the distance between X and Y. |
| [DDX](/spatial-editor-shader-graph-DDX) | Returns the partial derivative of the input value in the X direction of screen space. |
| [DDY](/spatial-editor-shader-graph-DDY) | Returns the partial derivative of the input value in the Y direction of screen space. |
#### Range
Sets the range of values.
| **Node** | **Description** |
| --- | --- |
| [Fractional](/spatial-editor-shader-graph-Fractional) | Returns the fractional part of a floating-point number. |
| [Clamp](/spatial-editor-shader-graph-Clamp) | Restricts the Input for each channel within the range of **Low** and **High**. |
| [Max](/8yadp090/rc2gkbqd) | Outputs the maximum value between **In 1** and **In 2**. |
| [Min](/spatial-editor-shader-graph-Min) | Outputs the minimum value between **In 1** and **In 2**. |
| [One Minus](/spatial-editor-shader-graph-OneMinus) | Subtracts the Input value from 1. |
#### Round
Performs rounding, ceiling, or floor operations on values.
| **Node** | **Description** |
| --- | --- |
| [Floor](/spatial-editor-shader-graph-Floor) | Outputs the nearest integer value less than or equal to the Input for each channel. |
| [Ceil](/spatial-editor-shader-graph-Ceil) | Outputs the nearest integer value greater than or equal to the Input for each channel. |
| [Step](/spatial-editor-shader-graph-Step) | If **In** < **Edge**, returns 0.0; otherwise, returns 1.0. |
| [Round](/spatial-editor-shader-graph-Round) | Rounds the Input for each channel to the nearest integer value. |
| [Sign](/spatial-editor-shader-graph-Sign) | The sign of each channel of the Input value: -1 for negative, +1 for positive, and 0 for zero. |
#### Geometry
Handles geometric and spatial data operations, such as vector operations, normal processing, direction transformations, and texture coordinate transformations.
| **Node** | **Description** |
| --- | --- |
| [Cross Product](/spatial-editor-shader-graph-CrossProduct) | Calculates the cross product of two Input vectors. |
| [Dot Product](/spatial-editor-shader-graph-DotProduct) | Outputs the dot product of two vectors. |
| [Magnitude](/spatial-editor-shader-graph-Magnitude) | Outputs the floating-point magnitude of a vector. |
| [Reflect](/spatial-editor-shader-graph-Reflect) | Calculates the reflection of one vector about another vector. |
| [Refract](/spatial-editor-shader-graph-Refract) | Calculates the refraction of the Input vector based on the given surface normal and refractive index (eta). |
| [Normalize](/spatial-editor-shader-graph-Normalize) | Outputs the normalized vector. |
| [Normal Map](/spatial-editor-shader-graph-NormalMap) | Converts a normal vector from object space or tangent space to world space. |
| [Rotate 2D](/spatial-editor-shader-graph-Rotate2D) | Rotates a 2D vector about the origin in two-dimensional space. |
| [Rotate 3D](/spatial-editor-shader-graph-Rotate3D) | Rotates a 3D vector around a specified unit axis vector. |
| [Place 2D](/spatial-editor-shader-graph-Place2D) | Transforms UV texture coordinates for 2D texture placement. |
#### Matrix
Performs matrix operations, such as matrix multiplication, transposition, inversion, and more.
| **Node** | **Description** |
| --- | --- |
| [Inverse Matrix](/spatial-editor-shader-graph-InverseMatrix) | Outputs the inverse matrix of a matrix. |
| [Determinant](/spatial-editor-shader-graph-Determinant) | Outputs the floating-point determinant of a matrix. |
| [Transpose](/spatial-editor-shader-graph-Transpose) | Outputs the transpose matrix of a matrix. |
#### Procedural
##### 2D Procedural
Generates 2D noise for a material.
| **Node** | **Description** |
| --- | --- |
| [Cell Noise 2D](/spatial-editor-shader-graph-CellNoise2D) | 2D cellular noise generator. |
| [Noise 2D](/spatial-editor-shader-graph-Noise2D) | 2D Perlin noise generator. |
| [Ramp 4 Corners](/spatial-editor-shader-graph-Ramp4Corners) | Four-point linear value gradient generator. |
| [Ramp Horizontal](/spatial-editor-shader-graph-RampHorizontal) | Left-to-right linear value gradient generator. |
| [Ramp Vertical](/spatial-editor-shader-graph-RampVertical) | Top-to-bottom linear value gradient generator. |
| [Split Horizontal](/spatial-editor-shader-graph-SplitHorizontal) | Splits the mask from left to right at the specified U value. |
| [Split Vertical](/spatial-editor-shader-graph-SplitVertical) | Splits the mask from top to bottom at the specified V value. |
| [Worley Noise 2D](/spatial-editor-shader-graph-WorleyNoise2D) | 2D Worley noise generator. |
##### 3D Procedural
Generates 3D noise for a material.
| **Node** | **Description** |
| --- | --- |
| [Cellular Noise 3D](/spatial-editor-shader-graph-CellularNoise3D) | 3D cellular noise generator. |
| [Fractal Noise 3D](/spatial-editor-shader-graph-FractalNoise3D) | Generates a 3D fractal noise centered around 0 by stacking multiple layers (octaves) of 3D Perlin noise with different frequencies and amplitudes. |
| [Noise 3D](/spatial-editor-shader-graph-Noise3D) | 3D Perlin noise generator. |
| [Worley Noise 3D](/spatial-editor-shader-graph-WorleyNoise3D) | 3D Worley noise generator. |
#### Transforms
Performs transformation operations on the texture, color, and other properties of the material.
| **Node** | **Description** |
| --- | --- |
| [Transform 2D](/spatial-editor-shader-graph-Transform2D) | Node that applies an affine transformation to 2D Input. |
| [Transform Matrix](/spatial-editor-shader-graph-TransformMatrix) | Transforms a vector using a matrix. |
| [Transform Normal](/spatial-editor-shader-graph-TransformNormal) | Converts a normal from one space to another. |
| [Transform Point](/spatial-editor-shader-graph-TransformPoint) | Transform coordinates from one space to another. |
| [Transform Vector](/spatial-editor-shader-graph-TransformVector) | Transform a 3D vector from one space to another. |
#### Trigometry
Performs trigonometric operations in material.
| **Node** | **Description** |
| --- | --- |
| [Cos](/spatial-editor-shader-graph-Cos) | The cosine value (in radians) of the input value. |
| [Sin](/spatial-editor-shader-graph-Sin) | The sine value (in radians) of the input value. |
| [Tan](/spatial-editor-shader-graph-Tan) | The tangent value (in radians) of the input value. |
| [Acos](/spatial-editor-shader-graph-Acos) | The arccosine value (in radians) of the input value. |
| [Asin](/spatial-editor-shader-graph-Asin) | The arcsine value (in radians) of the input value. |
| [Atan2](/spatial-editor-shader-graph-Atan2) | The arctangent value (in radians) of **In Y**/ **In X**. |
| [PI](/spatial-editor-shader-graph-PI) | Returns the mathematical constant PI (π). |
### Surface
Defines the final physical properties of the material surface and outputs them to the engine's rendering pipeline.
| **Node** | **Description** |
| --- | --- |
| [Preview Surface](/spatial-editor-shader-graph-PreviewSurface) | USD preview surface in MaterialX version. |
| [PBR Surface](/spatial-editor-shader-graph-PBRSurface) | Surface shader for physically based rendering (PBR) materials. |
| [Unlit Surface](/spatial-editor-shader-graph-UnlitSurface) | Surface shader for unlit materials. |
| [Occlusion Surface](/spatial-editor-shader-graph-OcclusionSurface) | Surface shader that defines properties for occlusion materials that do not receive dynamic lighting. |
### Vertex
Adjusts the position of model vertices.
| **Node** | **Description** |
| --- | --- |
| [Geometry Modifier](/spatial-editor-shader-graph-GeometryModifier) | Modifies the vertex properties of geometry based on input parameters, and executes once for each vertex. |
| [Vertex Index](/spatial-editor-shader-graph-VertexIndex) | Returns the index value of the current vertex in the mesh. |
### Other
| **Node** | **Description** |
| --- | --- |
| [Node Graph](/spatial-editor-shader-graph-NodeGraph) | A node that can contain shader nodes and other node graphs. |
| [Dot](/spatial-editor-shader-graph-Dot) | A relay node used for visual guidance of connections in the node graph. |
| [Named Dot](/spatial-editor-shader-graph-NamedDot) | Value relay node. Functions similarly to a global variable, used to pass data within the Node Graph. |
| [Debug Value](/spatial-editor-shader-graph-DebugValue) | Renders the input value onto the surface for display. |
| [Sticky Note](/spatial-editor-shader-graph-StickyNote) | A sticky note used to add annotations to the node graph. |
| [Environment Radiance](/spatial-editor-shader-graph-EnvironmentalRadiance) | Returns the diffuse radiance and specular radiance values of the environment based on real-world environmental information and an IBL map (which can be provided by the developer or use the default map). |
| [Instance Custom Data](/spatial-editor-shader-graph-InstanceCustomData) | Returns the floating-point value stored in the instance custom data. |
| [Fresnel Effect](/spatial-editor-shader-graph-FresnelEffect) | Calculates the Fresnel effect based on the direction of the surface normal and the view direction. |
