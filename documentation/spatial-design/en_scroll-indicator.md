Allow users to view content that is not visible in the View by sliding the Scroll Indicator. Helps users perceive content length and their current position, improving navigation efficiency and user experience. Its functional points are summarized as follows:

* Real-time updates while scrolling
* Adapt to horizontal and vertical scrolling scenarios
* In XR scenarios, because scrolling through long content can be demanding for users, systems should provide quick navigation.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/06dec382443e441a800a9110fa1d3bad~tplv-goo7wpa0wc-image.image)
Scroll Indicator is only displayed when the page is being scrolled. Therefore, it is recommended to visually indicate that the page is scrollable to guide users to view more content. For example, displaying partial content at the edge of the view indicates that more content is available in that direction.
## Location
The Scroll Indicator is fixed at the center of the Scroll View and is relatively small, which helps users scroll efficiently without needing to make large movements.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4851c4946d354ab9881a7262a7ae100a~tplv-goo7wpa0wc-image.image)
## Interactive behavior
Scroll Indicator provides feedback on the current position and total length. When interacting directly with the Scroll Indicator, if the drag speed reaches a certain threshold, the page turning speed can be increased.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3d35a8f315594895a4d0f21b3d022cc2~tplv-goo7wpa0wc-image.image)

1. Normal state
2. Hover state
3. Pinch state: Displays speed line

Although the overall size of the indicator is small, it is slightly thicker than indicators in other systems. If the spacing between the content and the view is too tight, consider increasing the outer margin to prevent the Scroll Indicator from overlapping with the content. When the Scroll Indicator overlaps with content, interactions with the Scroll Indicator take priority.
