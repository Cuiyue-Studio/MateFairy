By stacking multiple 3D Perlin noise layers (octaves) with different frequencies and amplitudes, a 3D fractal noise is generated that fluctuates around zero.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/72b23b6a2f544498b3113c364619c73c~tplv-goo7wpa0wc-image.image)
## Parameter descriptions

* **Amplitude**: The intensity of the generated noise. The higher the amplitude, the more pronounced the variations in the noise pattern.
* **Octaves**: The number of 3D Perlin Noise layers stacked by the node. The default value is 3.
* **Lacunarity**: The exponential scaling factor between each octave. This value determines the degree of difference between consecutive octaves (or Perlin noise layers). The default value is **2.0**.
* **Diminish**: The rate at which the amplitude decays for each subsequent octave. It is recommended to keep this parameter within the range of 0.0–1.0. The default value is 0.5.
* **position**: The three-dimensional coordinates used when reading data, which are used to map the texture onto the surface. By default, the current object space 3D coordinates are used.

## Node usage instructions
The **Fractal Noise 3D** node generates output by summing multiple layers (multiple octaves) of 3D Perlin Noise. The more octaves in Fractal Noise, the richer and finer the noise details. Each subsequent octave differs from the previous one, and this difference is determined by the **Lacunarity** and **Diminish** parameters.

* **Lacunarity** indicates the difference in frequency between octaves. The larger this value, the more uneven and less smooth the generated fractal noise tends to be.
* **Diminish** indicates how the amplitude changes between octaves. When this value is 1, the amplitude does not change; the smaller the value, the faster the amplitude decays between different octaves.

The following is a node graph example demonstrating how to use the **Fractal Noise 3D** node to generate a black-and-white pattern. Multiply the Input **Position** by a constant **Float**. This Float increases the frequency of the generated noise, causing the pattern to repeat more frequently.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b27adb2c586f458dbf8dad476a57be8e~tplv-goo7wpa0wc-image.image)
The example below shows the effect of applying the resulting texture to a cube, using different parameter values. All other parameters use their default values.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.33333333333333337);">

<div style="text-align: center"><strong>Octaves = 1</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/eb3ac0678bab46809c819cbb73fb80ff~tplv-goo7wpa0wc-image.image" width="1040px" /></div>




</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333333333333333);margin-left: 16px;">

<div style="text-align: center"><strong>Octaves = 3</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b1135834d3254a2894a98138a8e2e8de~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.33333333333333337);margin-left: 16px;">

<div style="text-align: center"><strong>Octaves = 5</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dbbd95b3026342da9f2e89844dc61d13~tplv-goo7wpa0wc-image.image)



</div>
</div>


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.34526172248803827);">

<div style="text-align: center"><strong>Lacunarity = 1</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e0f958ddfb75415885b95670a64a0c7c~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3213382775119617);margin-left: 16px;">

<div style="text-align: center"><strong>Lacunarity = 2</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/355d65a0518c426588eddcbfe16d5139~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

<div style="text-align: center"><strong>Lacunarity = 5</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dacd6994e5994a15ad0607bd3d1e32da~tplv-goo7wpa0wc-image.image)



</div>
</div>


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);">

<div style="text-align: center"><strong>Diminish = 0.2</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/707981a739de4410a6a4e10a8c062f86~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

<div style="text-align: center"><strong>Diminish = 0.5</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bc6683f272fc4e748a23706c3ab81b5c~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

<div style="text-align: center"><strong>Diminish = 1</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/43528b2e67854846a816d01353ccf565~tplv-goo7wpa0wc-image.image)



</div>
</div>


