Tool Bar  provides commonly used operations and controls for its main window. Ensure that the Tool Bar is easy to interact with and highly recognizable. Avoid providing too many options and actions, and consider grouping options for display.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5e9f4f8e146b4d38930a7f0da7baaa9e~tplv-goo7wpa0wc-image.image)
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/522d8584b181460caaa731c8b7125a2c~tplv-goo7wpa0wc-image.image)
## Location
The location of the Tool Bar in Plain and Volume is shown in the following table:
| **Application types** | **Location** | **Z axis elevation** | **Viewpoint following** |
| --- | --- | --- | --- |
| Plain | Align with the center of the bottom edge of the main window <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/df129eeb99f5465aaae1feca62350108~tplv-goo7wpa0wc-image.image) | Elevated 16dp above the plane window <br>  | ❌ |
| Volume | Align with the center of the bottom edge of Volume Front. <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4c4787f4a7424bb380c2079a8e32019b~tplv-goo7wpa0wc-image.image) | Elevated by 16dp relative to Volume Front (front) <br>  | ✅ |
## Composition
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bd9473cacfb94ba7a51bf043d2fa4a5f~tplv-goo7wpa0wc-image.image" width="2880px" /></div>


1. Container: The size automatically adjusts to the content. After reaching the maximum height, which is set to the window width minus 64 dp, the content will be clipped.
2. Operation item: No content restrictions. It is recommended to use icons or text, and to keep item types consistent within the same toolbar whenever possible.
3. Note: If the content of Item is an Icon, use a Tooltip to provide additional information about the operation.
4. Grouping: No limit on the number of groups. The grouping style is a divider line.

## Interactive behavior
### Configure menu
Menu can provide additional operations related to the Tool Bar, while retaining its original positioning characteristics.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1d3eea6b590c4466a7be09dbff5f6f75~tplv-goo7wpa0wc-image.image)
### Configuration sheet
Tool Bar supports triggering the Sheet through an operation, and the Sheet retains its original positioning characteristics.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e3e422f08c844643a6191cf1128bd287~tplv-goo7wpa0wc-image.image)
