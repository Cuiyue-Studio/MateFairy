Icons are used in the interface to represent common content, actions, and patterns. It is an indispensable element in the interface that partially replaces complex textual information and reduces the user's cognitive load. The icon style of the PICO Design System tends to be sharp and geometric, remaining clear and recognizable even at smaller scales. If your application uses the PICO Design System, follow these drawing specifications. If you use a personalized brand design, simply ensure that icons are consistent and readable.
## Design principles
#### Visual consistency
Icons within the same application must remain consistent in visual size, perspective, and style to facilitate user understanding and convey brand style.
As shown in the figure below, the icon style on the Tool Bar is consistent:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c4b0fe03eb304836aaa776f27e42e2d3~tplv-goo7wpa0wc-image.image)
#### Maintain semantic consistency
Within an application, each meaning is typically represented by a single graphic. For all representations of the shooting meaning, the camera icon should be used consistently; no other icons may be used. However, you can modify the attachments of the graphic to represent different states, for example, by using different numbers of sound waves to indicate the volume level.
As shown in the figure below, different variants of the same shape:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a609cd4397da487f87dca1c304326dea~tplv-goo7wpa0wc-image.image)
#### Maintain visual alignment within the graphic
Some icons, especially asymmetric ones, may appear visually unbalanced when centered or pixel-aligned. You can flexibly adjust the icon position so that it appears visually centered.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0c26c44bbff54eb3b60432aae530ae97~tplv-goo7wpa0wc-image.image)
#### The graphic contains textual information
If icons contain characters or represent information such as text formatting, reading order, and other information, ensure that these icons comply with localization standards during localization.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/57c1491bd3c6462ab263c91085f03fdc~tplv-goo7wpa0wc-image.image)
#### Usage format
To ensure rendering clarity, it is recommended to use the SVG format.
## Drawing specifications
#### Dimensions and grids
Try to keep icon content within the active area to prevent the image from being cropped or hidden. If additional visual weight is needed, content may extend into the padding area.

* Icon baseline sizes and areas description:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a6775e9974004792a0ae117bc03cae54~tplv-goo7wpa0wc-image.image)
* Example diagram:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fdd313bc62ec432f858cfb4cf1e19ffd~tplv-goo7wpa0wc-image.image)

Different shapes have inconsistent visual sizes; for example, a square looks larger than a triangle of the same dimensions. In such cases, it is necessary to appropriately increase the size of the triangle to ensure both shapes appear visually consistent. The sizes of different geometric shapes that ensure visual consistency are provided below. Simple icons are scaled down to 80% of these sizes.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/021f3fdeaa9f468cacaec76a8fa007dc~tplv-goo7wpa0wc-image.image)
#### Styling
The same graphic can have both linear and planar styles. You can choose different styles based on the usage scenario, but you must ensure that the structural design remains consistent between the two. Filled icons have greater visual weight than linear icons. When icons are organized hierarchically, filled icons occupy a higher level in the hierarchy than linear icons. For example, in the Tab Bar, unselected items can use linear icons, while selected items use filled icons.
| Linear and filled icons | Use filled icons for the selected state in the Tab Bar |
| --- | --- |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4f90b3ffd8114e8b84de143ff5adeb67~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3bce3da77ab144b188414a2e426cdc1e~tplv-goo7wpa0wc-image.image) |
#### Rounded corner
To align with the "warm and natural" design language of the PICO Design System, icons should be styled with rounded corners. The size of the outer rounded corners is affected by the dimensions of the graphic. Examples are listed below for clarification.

* Large rounded corners, 3 dp: Used for larger rectangles, forming the basic framework for icons.
* Medium corner radius, 2 dp: Used for smaller or secondary shapes, or in cases where a large corner radius is too rounded, or sharp corners are used.
* Small corner radius, 1 dp: Used for detail corners and similar areas.

The inner corner radius of a linear icon is equal to the outer corner radius minus the line width, and there is no corner cut (0 dp) between the internal lines and the external contour.
| Rounded corner examples | Inscribed angle: 0 dp | Concentric rounded corners |
| --- | --- | --- |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/39fd7c16816445c5b65dbf3dd8b659c7~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fbeb3b69a821436a94abd33469f0bc15~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8ae7eefa0eb54b6eb02aac34c621f572~tplv-goo7wpa0wc-image.image) |
#### Lines
The commonly used line thickness is 2 dp, with square endpoints and square corners. Rounded corners are used when the angle is too small.
| Line thickness can be adjusted flexibly | Bends and endpoints |
| --- | --- |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8748b7c0a7884b5bb709ed7d8fef8373~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e21c9d7155a24fcc8c225949ff0d3241~tplv-goo7wpa0wc-image.image) |
#### Slashes and incisions
The slash representing delete and cancel should be oriented from `top left to bottom right` at a 45˚ angle, spanning the entire icon. The diagonal line thickness is 2 dp, and the notch is set to 1.5 dp by default. However, if this affects the recognizability of the main object, the notch can be flexibly adjusted.
Incisions and inclination angles are in integer multiples of 45 degrees.

| Cutout size defaults to 1.5 dp | Incision size must be a multiple of 45 degrees | Inclination angles are multiples of 45˚ |
| --- | --- | --- |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/58bbbfef76564f29b7606dd1a55e8bd5~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/66c2ff239b0b4d278ac420b3fb38864e~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c096406b7fb044ffb953423fcf5d7867~tplv-goo7wpa0wc-image.image) |
#### Surfaces and negative space
To ensure visual size consistency, the base shape of filled icons should be slightly smaller than that of linear icons. To improve recognizability, negative shapes can be appropriately enlarged. The minimum line thickness for the main graphic is 1.5 dp.
| Enlarge the negative shape appropriately | Complex icons cannot be directly used to fill linear icons. | Ensure the recognizability of the main graphic |
| --- | --- | --- |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/398642222f174e2c896c449e261c1b60~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4671a5570f3342a298642986cb628658~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c9c9155c575346999b2db0387a177879~tplv-goo7wpa0wc-image.image) |
#### Badge
The badge is displayed by default at the bottom right corner of the main element. There should be a certain amount of spacing between the badge and the main body (default is 2 dp), and try to ensure that the cutout and badge outline remain parallel as much as possible. The badge should avoid important areas of the icon and should not affect recognition of the main graphic. Its position can therefore be adjusted flexibly.
| Badge recommendation examples | Add a straight-line notch when the badge outline is too complex. | Do not cover the subject with the badge |
| --- | --- | --- |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/af46e132db604ba4973fb552e9f01e92~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a7d3f01bc2ec48e69f5f78da68aa4097~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d9147e66e4074592a6b2a63b2c4f40e2~tplv-goo7wpa0wc-image.image) |

