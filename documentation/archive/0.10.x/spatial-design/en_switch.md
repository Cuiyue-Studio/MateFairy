A toggle switch is a switch that toggles between two modes, namely on and off. Depending on different events, the toggle switch can be preset to either the on or off state for users. A toggle switch is typically used to set controls.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7dd351c963e045d3b51593cdff350378~tplv-goo7wpa0wc-image.image)
## Anatomy
Toggle buttons usually exist independently and do not have text.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/17db61958a8f416fb9a0d95bea1ba1fc~tplv-goo7wpa0wc-image.image)

1. **Switch track**
   The switch track is a rounded rectangular bar, which serves as the fundamental supporting structure for the switch. The overall state (on or off) is visually indicated by changes in color or style.
2. **Switch slider**
   A movable circular or rounded rectangular component located above the track. Enhancing state awareness through changes in position is the core trigger point for interaction.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/929f0ed9163a4bebb121d678059ea696~tplv-goo7wpa0wc-image.image)

## Interactive behavior

* **Interaction methods:**
   When users click the track or thumb of the switch, the state is toggled directly between "On" and "Off". A second confirmation is generally not required, although some special requirements may call for a second confirmation.
* **Interactive Hotspots:**
   The operation hot zone size is 40 × 36 dp.
* **Visual feedback**
   * Status color differences
      * When enabled: The track displays a highlight color (blue), and the slider moves to the active side (right side).
      * When off: the track is displayed in gray, and the slider moves to the inactive side (left)
      * When disabled: The entire element is grayed out (30% opacity), does not respond to clicking or sliding, and it is clear that it cannot be operated.
   * Slider position synchronization: The slider is always precisely positioned according to the state
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e80f4445840f47358c5f7c643b1e1929~tplv-goo7wpa0wc-image.image)


