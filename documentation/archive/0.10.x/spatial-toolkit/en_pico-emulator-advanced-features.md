This article introduces the controller button mode of PICO Emulator.
Click the controller button icon in the left toolbar to enable controller button mode. After enabling, PICO Emulator will pop up the **controller settings** window, prompting you to confirm the current controller and button mapping. **Caution: The controller button mode is not active at this time**.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.0715962441314554);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5cf31cb4903140329fcbfe3fd9ee83bd~tplv-goo7wpa0wc-image.image)






</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.9284037558685446);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9e73fb08079f4fc0ab14c17bf6409212~tplv-goo7wpa0wc-image.image)



</div>
</div>

To activate the controller button mode, you also need to switch the operation mode in the lower right operation area to **Left Controller Mode** or **Right Controller Mode**. Once the controller button mode is active, PC keyboard keys will be mapped to the virtual controller's buttons according to the configuration in the **settings** panel **controller settings**.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e48db3fc4e794224b7c95b5c268010fb~tplv-goo7wpa0wc-image.image)
## **Freely move the controller**
You can control the movement of the controller using the following shortcuts:

* Shift+W: Move the controller forward
* Shift+A: Move the controller left
* Shift+S: Move the controller backward
* Shift+D: Move the controller right
* Shift+Q: Move the controller down
* Shift+E: Move the controller up

After moving the controller, the ray collision point of the controller will no longer coincide with the mouse pointer. The mouse pointer is only used to control the direction of the controller's ray. You need to determine the currently selected object based on the object hit by the controller's ray in the virtual scene.
The following figure shows the relationship between the controller's ray collision point and the mouse pointer after moving the controller forward.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d8de3adce83e4144909171be9b903150~tplv-goo7wpa0wc-image.image)
## **Customize ray deflection**
By default, the direction of the controller's ray in PICO Emulator aligns with the controller's own Z-axis direction (that is, straight ahead, see the blue arrow in the figure below). However, to optimize the user's grip experience (in line with ergonomics and to avoid excessive wrist bending), application developers often actively adjust the ray angle by rotating the controller model or other means. This ray deflection causes the simulator's mouse pointer position (which by default corresponds to the controller's straight-ahead direction) to not match the actual ray landing point in the game.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dbb7419c5a6e4356a3ca9f3b509ebba4~tplv-goo7wpa0wc-image.image)
To use such applications properly with the controller in PICO Emulator, you need to go to the **Settings** panel's **Controller Settings** page and manually adjust the ray angle until the mouse pointer position in the emulator matches the actual ray landing point in the application.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b86a632da9b54cc49f459f216ec1861f~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b177e42945a54bb395947048ca82d73b~tplv-goo7wpa0wc-image.image)



</div>
</div>


