The projection mode determines how visual information from a 3D space is mapped onto a 2D plane for storage and transmission. It can generally be divided into two categories: planar projection and spherical projection.
## Planar projection
Traditional flat video typically uses perspective projection to simulate the visual principles of the human eye or camera lens, producing a natural effect where objects closer appear larger and those farther away appear smaller. Its field of view typically ranges from 60° to 120°, making it suitable for standard film and television shooting and playback.
## Spherical projection
When displaying 180° or 360° panoramic videos or creating an immersive experience, spherical projection must be used. Common methods include equirectangular projection (ERP), cube map projection (CMP), and equi-angular cubemap (EAC).
The core challenge of spherical projection is how to effectively unfold 3D information from a sphere onto a 2D plane. Regardless of the method used, this 'flattening' process inevitably causes distortion. However, the distribution and type of distortion vary depending on the projection mode, so it is necessary to select an appropriate projection mode based on the use case.
#### ERP
ERP unfolds the spherical surface into a rectangle using latitude and longitude, similar to how a world map is created. The advantages are simple implementation, excellent compatibility, and almost all media players and editing software support it. The drawback is that there is severe stretching in the polar regions, which not only degrades visual quality but also wastes pixel resources.
#### CMP
CMP projects the sphere onto the six faces of a cube, which can effectively avoid stretching in the polar regions. The advantage is that the pixel utilization rate is relatively high. The disadvantages are that pixel density at the edges and corners is excessively high, which can easily result in seams or breaks, and support for standard video encoding is poor, requiring additional processing.
#### EAC
EAC is an improved version of CMP. It maps the sphere onto the six faces of the cube using angular subdivision rather than linear distribution, resulting in more uniform pixel coverage. The advantage is higher coding efficiency, allowing more useful information to be delivered at the same bitrate. The disadvantage is that it relies on dedicated codecs and has higher computational complexity, so it is not the best choice in scenarios where compatibility or real-time performance is a priority.
## Comparisons
The differences among various projection modes in terms of basic principles, advantages and disadvantages, and suitable applications are as follows:
| **Projection mode** | **Basic principle** | **Advantages** | **Disadvantages** | **Use cases** |
| --- | --- | --- | --- | --- |
| Perspective projection | Project the 3D scene onto a plane. | * Low image distortion <br> * Simple rendering and display | Does not cover 360° FOV; poor immersiveness | * Standard video <br> * Traditional camera photos <br> * Plane |
| ERP | Map spherical latitude and longitude onto a rectangle with a 2:1 aspect ratio. | * Easy to implement <br> * Good compatibility <br> * Standardized storage and transmission | * Severe stretching in polar regions <br> * Uneven pixel distribution | * Standard 360° video <br> * Commercial apps with high compatibility requirements <br> * video content distributed at a large-scale |
| CMP | Project the sphere onto the six faces of the cube, distributing the projection linearly. | * More reasonable pixel distribution, which alleviates the problem of polar region stretching <br> * Less pixel wastage and less distortion | * Excessive pixel density at the edges and corners, causing noticeable seam issues at the edges. <br> * Low support for standard video encoding, requiring additional processing. | * Real-time environment map rendering in game engines <br> * Reflection and panoramic images in VR/AR engines |
| EAC | Project the sphere onto the six faces of the cube, and perform uniform angular sampling instead of using a non-linear distribution. | * More balanced pixel distribution, which reduces polar region stretching and edge seam issues. <br> * Higher coding efficiency | * Relies on dedicated codecs, has complex encoding and decoding logic, and features low compatibility and low real-time performance <br> * Some seam issues remain | * High-quality VR content <br> * Professional 360° video app |
## How to choose an appropriate projection mode?
When selecting a projection mode, it is necessary to consider factors such as compatibility, encoding efficiency, and visual effects. You can refer to the comparison table below to select the appropriate projection mode for your video.
|  | **Perspective projection** | **ERP** | **CMP** | **EAC** |
| --- | --- | --- | --- | --- |
| **Compatibility & standardization level** | Maximum | Maximum | Intermediate | Lower |
| **Visual quality** | High | Intermediate | High | High |
| **Encoding efficiency** | Maximum | Intermediate | High | Intermediate |
| **Processing complexity** | Minimum | Low | Intermediate | High |
| **Storage space** | Small | Large | Intermediate | Intermediate |
| **Real-time performance** | Best | Good | Intermediate | Best |
## How to obtain videos in different projection modes?
You can obtain videos with different projection modes through the following three methods:

* Direct shoot videos: Use professional equipment to shoot videos in native format.
* Convert existing videos: Use software or command-line tools to perform format conversion.
* Obtain from existing platforms: Download or purchase ready-made video content.
