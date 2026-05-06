The application icon is an important medium for application identification, serving a dual role in conveying brand image and facilitating function identification. This page introduces the basic guidelines for app icon design, including grid layout, color, and decorative elements, to help developers create icons that are simple, consistent, and easily recognizable, and to ensure optimal display on PICO devices and in various usage environments. If you need help with application icon design, you may refer to this specification and design resources.
## Graphic design
### Grid
The grid layout of PICO application icons consists of three parts:

* **Central area**:
   * The core part of icon design is typically the location where the most important and recognizable element of the icon is placed.
   * Main graphics, logos, or symbols should be placed in this area.
* **Expansion area**:
   * The space surrounding the central region is used to accommodate auxiliary design elements or details.
   * Auxiliary elements may be placed in the expansion area to enhance the icon's layering and richness.
* **Buffer area**:
   * The blank area between the edge of the icon and design elements.
   * Whitespace plays a critical role in the design of application icons for PICO OS 6. It provides icons with breathing room, prevents visual crowding and a sense of visual oppression, and, except in special cases, priority should be given to maintaining sufficient blank space in the margin area.


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.22887323943661972);">

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2164aeff104f4b49bda496ae192fc1b4~tplv-goo7wpa0wc-image.image" width="400px" /></div>



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.7711267605633803);margin-left: 16px;">

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/caf796b4a6704f92832fe26b850a5d4b~tplv-goo7wpa0wc-image.image" width="800px" /></div>



</div>
</div>

Please use the standard grid layout in the app icon design template to ensure consistent visual appearance of the icon across different sizes and application scenarios.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/317740601d5748c29cd42c6d47db51da~tplv-goo7wpa0wc-image.image)
### Shape
For icon elements with different proportions, refer to the standard icon grid layout when drawing them to ensure a consistent visual weight.
The primary function of the grid layout is to serve as a reference for volume; some icons may break through the grid boundaries when necessary for a sense of volume.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dee5b4e0fb2f480cad8efcd26b92bfa3~tplv-goo7wpa0wc-image.image)
## Decorative elements
Concise and consistent icon design not only improves user recognition efficiency in virtual reality environments, but also enhances the overall aesthetics and professionalism of the interface, optimizes the interaction experience, reduces visual distractions, and enables users to focus more on the content itself. Circular icons are visually more compact, leaving less space for additional decorative elements such as corner badges and banners. This can easily lead to visual crowding and overlapping information, which affects the recognition of the icon itself.
Therefore, it is recommended to avoid using additional decorative elements such as badges and banners when designing application icons. If there are special requirements, please follow the design principles below:

* Do not allow badges or banners to cover the top of the app icon
   The top area of a circular icon is relatively small and more likely to be obstructed. To maintain the integrity and recognizability of the icon, it is not recommended to cover the top area of the app icon with badges or banners.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c346fd48f27544918d418db0d076b1e3~tplv-goo7wpa0wc-image.image)
* Recommended placement area
   It is recommended to place corner badges or banners at the bottom, provided that the main part of the icon is not obscured.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/984f8289a0d54243bec4c127969a01aa~tplv-goo7wpa0wc-image.image)

## Icon resource specifications
To ensure consistent icon display within the system, the application's preset icon resources should meet the following criteria.
| **Output size** | **Shape** | **Layering** | **Format** | **Color mode** |
| --- | --- | --- | --- | --- |
| 1024 * 1024 px | Circle | Single layer | PNG | It is recommended to use Display P3. |

