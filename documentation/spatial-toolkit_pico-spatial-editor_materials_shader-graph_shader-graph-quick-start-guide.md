This tutorial introduces how to use Shader Graph to create a glowing sphere material. The entire process requires only a sphere and a few nodes. Ultimately, this material will combine a base color with a brighter edge lighting effect.
Additionally, this tutorial will cover how to use Shader Graph to implement a pulsing animation for the glowing sphere.
## Preparation
Create a sphere in PICO Spatial Editor to associate with the Shader Graph material.

1. Create a Spatial Editor project.
2. In the **Hierarchy** window, select **+ > Primitive Shapes -> Sphere** to create a sphere.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1752470107cc417aba257de0c96a0adb~tplv-goo7wpa0wc-image.image)
You can view the created sphere in Spatial Editor.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/81efe41d0df444769d0c613ea8dd5ace~tplv-goo7wpa0wc-image.image)
## Step 1: Create a basic Shader Graph
First, create a minimal working Shader Graph so the sphere displays a base color.

1. Open your Spatial Editor project.
2. Select the sphere you created, then choose the **Shader Graph** tab at the bottom of Spatial Editor. Click **Create Material** to create a Shader Graph material under the sphere. Shader Graph includes a **Preview Surface** node and an **output** node by default.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/309322e9f1534cac98ad08eaae470439~tplv-goo7wpa0wc-image.image)
3. Rename the Shader Graph material you created, for example, `GlowMaterial`.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ff9d780661614d9aa20c6f7ad960f008~tplv-goo7wpa0wc-image.image)
4. In the Shader Graph tab, click the **Input Node** panel's **+** button on the left to add an Input node of type **Color 3**. Rename it to **BaseColor**, and set the value of this Input node in the **Shader Graph Inspector** window on the right. Blue, cyan, or purple are all suitable for glowing spheres. Therefore, you can set the RGB (0-1) values of the **Color 3** node as follows:
   * R: 0.2
   * G: 0.6
   * B: 1.0
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dd4b185e94a24cd2b59d9bc4ae02b9ae~tplv-goo7wpa0wc-image.image)
5. Connect the **BaseColor** Input node to the **Preview Surface** node's **Diffuse Color** input, then save the Shader Graph.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c1113d2429984436aa61a79d1c0f34a9~tplv-goo7wpa0wc-image.image)
6. Select the sphere in the **Hierarchy** window, then find the **Inspector** window on the right. Locate the **Material Bindings** component, set **Binding** to the Shader Graph material you created, and apply the material to the sphere in the scene.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/896fa62706c64425b7a7f14477461c87~tplv-goo7wpa0wc-image.image)

At this point, you should see a **solid color sphere**.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d42b4f747cc948d08b04fd3ad280f5eb~tplv-goo7wpa0wc-image.image)
## Step 2: Add edge lighting
Use an edge effect node to generate areas that are brighter near the outline.

1. In the Shader Graph tab, click the **Input Node** panel's **+** button on the left to add the following Input nodes. Rename these Input nodes and set their values in the **Shader Graph Inspector** window on the right.
   | **Node** | **Name** | **Value** | **Function** |
   | --- | --- | --- | --- |
   | Color3 | GlowColor | A bright cyan-blue or white-blue is recommended. In this tutorial, its RGB (0-1) values are set to: <br>  <br>    * R: 0.63 <br>    * G: 0.77 <br>    * B: 0.92 | Controls the edge glow color. |
   | Float | GlowIntensity | 2.0 | Controls the glow intensity. |
   | Float | EdgePower | 3.0 | Controls the width and sharpness of the edge light. |
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6d459810f21041a38541421cc112a4d7~tplv-goo7wpa0wc-image.image)
1. Add a **Fresnel Effect** node. Connect the **EdgePower** Input node to the **Fresnel Effect** node's **Power** input.
   The **Fresnel Effect** node is essentially an angle calculation node. It outputs a value that varies around 0 to 1 based on the surface normal (**Normal**), view direction (**View Dir**), and intensity (**Power**). This value is usually lower on the front and higher at the edges.
   The value of **Power** affects the distribution of the edge light:
   
   * The lower the value, the wider the edge range.
   * The higher the value, the thinner and more concentrated the edge.

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d4911b8f847542c8afacc2b19439d39a~tplv-goo7wpa0wc-image.image)
2. Add a **Multiply** node. This allows you to tint the edge effect with the color you want.
   * Input **In 1**: Output from the **GlowColor** Input node
   * Input **In 2**: Output from the **Fresnel Effect** node
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/73d9ff8d9cc14f91b23f68aac7c72c7a~tplv-goo7wpa0wc-image.image)
3. Add another **Multiply** node. This allows you to control the glow brightness independently without affecting the base color.
   * Input **In 1**: Result from the previous **Multiply** node
   * Input **In 2**: Output from the **GlowIntensity** Input node
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/73854505bdd7496499ad96378055bd5e~tplv-goo7wpa0wc-image.image)

## Step 3: Merge the base color and edge glow
Since in Step 1, you have already connected the output of the **BaseColor** Input node to the **Preview Surface** node's **Diffuse Color** input, you only need to connect the final output from Step 2 to the **Preview Surface** node's **Emissive Color** input to merge the base color and edge glow.
**Diffuse Color** determines the color displayed when the object is lit, while **Emissive Color** determines the color of the light emitted by the object itself. Therefore, the base color is connected to **Diffuse Color**; the edge light is connected to **Emissive Color**.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/47b83bb3301d468585db1281c13c0e84~tplv-goo7wpa0wc-image.image)
At this point, you should see:

* The sphere has a base color
* The sphere's edge is brighter
* It looks like a simple energy ball or glowing sphere

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5519a605a0634719bf01940ea994394a~tplv-goo7wpa0wc-image.image)
Next, you can also fine-tune the following output parameters in the **GlowMaterial** **Inspector** window to experience different effects.

* **BaseColor** Input node: Controls the main color of the sphere.
* **GlowColor** Input node: Controls the edge glow color.
* **GlowIntensity** Input node: Controls the overall glow intensity.
* **EdgePower** Input node: Controls the edge range.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b548640af9384fe0a3c9c6d034da0926~tplv-goo7wpa0wc-image.image)
## Step 4: Add a pulse animation
To make the glowing sphere more dynamic, you can add a simple pulse animation.

1. First, add two Input nodes to control the pulse effect. The existing **GlowIntensity** Input node will be used as the base brightness value.
   | **Node** | **Name** | **Value** | **Function** |
   | --- | --- | --- | --- |
   | Float | PulseSpeed | 2 | Controls the pulse speed |
   | Float | PulseAmplitude | 0.8 | Controls the amplitude of brightness variation |
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7acbffce34c14731ae9cd0c3de1f103d~tplv-goo7wpa0wc-image.image)
2. Add a **Time** node. This node continuously outputs a value that increases over time. Do not connect it directly to the glow intensity yet, otherwise the glow intensity will only keep increasing and will not oscillate back and forth.
3. Add a **Multiply**  node to control the pulse speed.
   * Input **In 1**: Output from the **Time** node
   * Input **In 2**: Output from the **PulseSpeed** Input node
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0ce8b13bd1ff4efc89ed6052b303cf05~tplv-goo7wpa0wc-image.image)
4. Add a **Sin** node. Connect the output from the previous **Multiply** node to the input of the **Sin** node. Now you get a value that repeatedly varies between `-1` and `1`: sin(**Time** × **PulseSpeed**). This is the waveform of the pulse.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1ae9819494ce43d9a73ba15099325988~tplv-goo7wpa0wc-image.image)
5. Add another **Multiply** node to control the amplitude of the fluctuation. This step scales the waveform to the desired range: sin(**Time** × **PulseSpeed**) × **PulseAmplitude**.
   * Input **In 1**: Output from the **Sin** node
   * Input **In 2**: Output from the **PulseAmplitude** input node
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/62030591709641559f1f2c790841a897~tplv-goo7wpa0wc-image.image)
6. Add an **Add** node. Adjust the fluctuating values to be near a normal brightness baseline. Final result: sin(**Time** × **PulseSpeed**) × **PulseAmplitude** + **GlowIntensity**.
   * Input **In 1**: Output from the previous **Multiply** node
   * Input **In 2**: Output from the **GlowIntensity** input node.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3a79aff8ffa04fa8ac334b834eed40d5~tplv-goo7wpa0wc-image.image)
7. Replace the output of the **GlowIntensity** node with the output of the **Add** node, and connect it to the **Multiply** node's **In 2** input.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/272ef6d1d66d48829da768c560b0045a~tplv-goo7wpa0wc-image.image)

At this point, you can see the glowing sphere effect:

* The sphere's edge continuously glows
* The glow intensity gradually increases and then decreases

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4ca2430cb9f34256bd901d4ab76a3968~tplv-goo7wpa0wc-image.image)
Next, you can fine-tune the following output parameters in the **GlowMaterial** **Inspector** window to experience different effects.

* **GlowIntensity**: Base brightness
* **PulseAmplitude**: Fluctuation amplitude
* **PulseSpeed**: Change speed

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/61aaa53f1820425990e52db9766b1aa3~tplv-goo7wpa0wc-image.image)
## Next, you can
You have now created a simple Shader Graph. Next, you can learn how each node in the Shader Graph works. For details, see [Shader Graph Node Overview](/shader-graph-node-overview).

