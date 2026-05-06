Side Navigation is placed at the leading edge of the window; for left-to-right languages, it appears on the left side, while for right-to-left languages, it appears on the right side. It provides a flat view of the application's information hierarchy, allowing users to quickly access content at the same level.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2b5bc95e901944268ef67dbf5641fc88~tplv-goo7wpa0wc-image.image)
Because Side Navigation cannot be collapsed, it occupies a relatively large amount of space. When the window size is small or more space is needed to display other information or features, you can use more compact navigation controls such as [Tab Bar](/en_tab-bar). When the application hierarchy is more complex, you can use Tab Bar together with Side Navigation. In this case, Side Navigation serves as secondary navigation.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a30de705e7104da897b7e87a0d7cf575~tplv-goo7wpa0wc-image.image)
## Anatomy
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0dab5f68917a40b5830df5c7b1059259~tplv-goo7wpa0wc-image.image)

1. Container: Height matches the window; width is available in two sizes.
2. Title (optional): A concise description of the navigation page
3. Search box (optional): The search method and result display location are defined by the developer.
4. Leading icons: There are no restrictions on image format. SVG is recommended.
5. Label text: Try to avoid text that is too long and causes automatic line breaks. If you need to insert a line break, you can create a break between words or use a hyphen for longer words.
6. Tail element (optional): Can be an Icon Button that supports independent interaction or a non-interactive Badge.
7. Category titles (optional): There is no limit to the number of categories.

### Dimensions
Display height matches window height; if content exceeds the display height, scroll to view more. Two width options are available: 272 dp and 316 dp. You can select the appropriate width based on the window width. For example, when the window width is 1280 dp, it is recommended to use 316 dp.
### Grouping
Provides two grouping styles: spacing and heading. The spacing grouping style is commonly used for simple categorization, and regular users cannot modify the navigation content. Title grouping is commonly used in tool management applications, which typically also allow users to customize group content and titles.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3610200a0b4544ea95fe4c66cd79ebde~tplv-goo7wpa0wc-image.image)
## Interactive behavior
### Slide
There is no limit to the number of navigation items. When the navigation exceeds the visible area, scroll to view all content. The header of Side Navigation remains fixed at the top.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4e80ca6b030e47f0a67039e5cebe957b~tplv-goo7wpa0wc-image.image)


