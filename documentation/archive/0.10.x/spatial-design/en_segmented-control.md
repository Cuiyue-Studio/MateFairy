Segmented Controls are commonly used to select options and switch views. Avoid using Segmented Controls for actions such as adding, removing, or editing content.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/36bad46b8ad64839b1059b28586e1920~tplv-goo7wpa0wc-image.image)
Limit the number of segments in Segmented Controls to between 2 and 7, as having too many segments increases user cognitive load and navigation time. Each segment is the same size, so the length of the text within each segment should not vary significantly.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bbc66ee6b13142b2a660382e94da7d3d~tplv-goo7wpa0wc-image.image" width="4000px" /></div>

## Anatomy
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cfffbfbe048a4c029a14748ab88bf7ce~tplv-goo7wpa0wc-image.image)

1. Container: Developers customize it according to the upper-level container.
2. Item section: Content can be Text, Icon, or Icon + Text. Avoid mixing icon-only labels with text labels.

## Interactive behavior
Segments in Segmented Controls cannot be disabled (no Disabled state). Only single selection is currently supported; the selected item will be highlighted.

