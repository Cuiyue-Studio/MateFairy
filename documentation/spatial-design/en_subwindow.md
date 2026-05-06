A Sub Window supplements the content of the main Window and is commonly used to display auxiliary information, such as app reviews in a store, video bullet comments, and so on.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/42e76a2a6b43405e8d1c11766b77c614~tplv-goo7wpa0wc-image.image)
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/09a647ecb4934ef3ac9d0d61041d7cb2~tplv-goo7wpa0wc-image.image)
## Location
The position of Sub Window in Plain and Volume is shown in the following table:
| **Window** <br> **types** | **Location** | **Viewpoint following** |
| --- | --- | --- |
| Planar | LTR UI: On the right side of the main Window <br> RTL UI: On the left side of the main window <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/723724aab2504dfc9745b1c0f361dca2~tplv-goo7wpa0wc-image.image) | ❌ |
| Volumetric | LTR UI: On the right side of the main Volume Front  <br> RTL UI: On the left side of the main Volume Front <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b6757e603dc54bc4a221838b206fbea5~tplv-goo7wpa0wc-image.image) | ✅ |
## Angle
The default rotation angle of Sub Window is 0. When the combined width of the Main Window and Sub Window exceeds the maximum visible area, the Sub Window can be rotated around a Y axis positioned close to the Main Window to provide a better user experience.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f398659cee594af7af318c77d36c375b~tplv-goo7wpa0wc-image.image)
## Anatomy
Sub Window is a container and does not restrict its contents. It comes with a default material that developers cannot modify.
## Interactive behavior
### Resize
During the resize process, the width of the Sub Window remains unchanged, while its height matches that of the main Window; its position relative to the main Window also remains unchanged.

