A Slider is a horizontal control track featuring a movable component called the "thumb," which users can drag to select a value or range.

* Intuitiveness: Visual tracks and draggable slider heads enable users to quickly understand the adjustable range and the currently selected value.
* Precision can be controlled: You can balance operational efficiency and selection accuracy by adjusting scale and step size settings, such as increasing a fixed value with each drag.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/100e3ccc2eee4fb69a4d28dbbe0cf187~tplv-goo7wpa0wc-image.image)
## Anatomy
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8e23f71bf89647f58516e80320751e1b~tplv-goo7wpa0wc-image.image)

1. **Track:**
   The container for the slider, used to define the range within which the user can drag. The range can be divided into a selected region and an unselected region. For example, the area from the starting point to the current position is the selected region, while the remaining area is the unselected region. These regions are distinguished by color, so that the currently selected range can be intuitively presented.
2. **Knob Box:**
   A draggable interactive element that allows users to change the value by clicking or dragging this element. It is designed with a circular shape to make it easier for users to operate. This element supports displaying the current value in a tooltip style when the slider thumb is hovered over or dragged, enhancing the interactive experience.
3. **Icon:**
   Supports displaying auxiliary elements, such as icons, at the starting point to help users easily and quickly understand the purpose of the slider.
4. **Dot:**
   Auxiliary markers on the track can only switch between preset discrete value points and cannot select arbitrary intermediate values.

It supports two types of sliders: Stepped Slider and Continuous Slider. The main difference lies in the continuity of value adjustment and the method of precision control:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cd4e58811573412c87aeffa653283432~tplv-goo7wpa0wc-image.image)
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fa075baf403e46a2a70eaaa9a9fe026c~tplv-goo7wpa0wc-image.image)
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bf40678cbb3947c2ab4c7e8134498f3c~tplv-goo7wpa0wc-image.image)
| **Dimension** | Stepped Slider | Continuous slider |
| --- | --- | --- |
| **Numerical properties** | Discrete values (fixed step size) | Continuous values (any value in between) |
| **Scale design** | Clarify the correspondence between scale marks and selectable values | No scale or only reference marks |
| **Operation accuracy** | High (strictly matches preset value) | Flexible (supports fine adjustment) |
| **Typical scenarios** | Controller vibration adjustments | Volume and brightness |
### Dimensions
There are three sizes: Small, Regular, and Max. For Regular and Max, an icon can be configured on the left side.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/eef33553917c434ea96effa30726e9ed~tplv-goo7wpa0wc-image.image)
## Interactive behavior
### Interaction methods
**Stepless adjustment:**

* The user clicks the Knob Box and drags it along the Track to change the value. Releasing the button confirms the selection.
* You can click any position on the track directly; Knob Box will jump to the corresponding position and update its value, improving operational efficiency.
* During movement, if the progress for the **Regular** size is less than 40 dp, there is no Dot in the Hover and Pressed states. This is consistent with the Normal state.
* During movement, if the progress for the **Max** size is less than 80 dp, there is no Dot in the Hover or Pressed states. This is consistent with the Normal state.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3641eb0ca9f146dc80188f3626fe1d05~tplv-goo7wpa0wc-image.image)
**Pole adjustment available**

* Drag snapping: When dragging the slider button (Knob Box), it does not move smoothly with the interaction. Instead, it snaps to the preset tick marks. If the distance between two knobs is less than 50%, snapping will occur to the other knob; as shown in the diagram below, snapping occurs at the third point.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f5a0dfa7b37e4838a25b55d3cc38a499~tplv-goo7wpa0wc-image.image)
* Click to jump: When you click a tick mark or blank area on the track, the slider thumb will jump directly to the nearest valid value point, rather than to any arbitrary position (to prevent selecting invalid values).

### Operation hotspot
There are two types of heating zones: those with stepped adjustment and those with stepless adjustment.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3262c51e10a64292a07215acd8d52e09~tplv-goo7wpa0wc-image.image)
### Visual feedback
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3d582334589b46208da9ba57a012feb2~tplv-goo7wpa0wc-image.image)
