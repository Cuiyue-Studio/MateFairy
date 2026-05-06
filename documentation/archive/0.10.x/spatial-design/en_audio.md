On the device, you can design sound for **immersive scenarios**, process different types of audio content, implement random logic relationships, and apply layered scene processing to further enhance the sense of presence and immersion. You can also design sound for **apps**, upload audio content according to your needs, and the content for different apps also varies. Tool-type apps focus more on efficiency-oriented sound effects, while consumer apps place greater emphasis on entertainment-oriented sounds. This article provides design concepts and technical standards for sound.
## Define the sound content
### Immersive scenarios

* What is spatial audio
   Spatial audio is not a specific technology, but rather a set of standards that enhance the immersive sensation of sound. Compared with traditional audio, spatial audio enhances the immersive audio experience by presenting sounds from different directions within a space. From the distant clamor of the city and the cries of seagulls on the rooftops to the wild, primal energy of the crowd at a live performance. These sounds are not merely a supplement to what we see; they place us within the landscape, bring things to life, and at the same time highlight their positions around us.
* The structure of spatial audio


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.32851531100478465);">

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e2a22268ea924f1eb02d53e8b9c3c429~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.33330000000000004);margin-left: 16px;">

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9e3017f8cdde4e6aa7d527f3b699cebd~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.33808468899521527);margin-left: 16px;">

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a6a51ab2a28749d1bcdc07b561aa9853~tplv-goo7wpa0wc-image.image)


</div>
</div>


   * **Spatial Audio** (object audio): In a real environment, when a phone call comes in, we can accurately locate the phone based on this object audio.
   * **Ambient Audio** (environmental audio): In a real outdoor environment, when the wind blows, we can sense the wind all around us.
   * **Channel Audio**: Put on headphones, listen to music, or hear your own voice when speaking.
* The content required for immersive scenarios
   * Scene analysis and hierarchy breakdown
   * The significance of different relationships in a scenario for the sound.
      * Background: Can be designed to create an environmental atmosphere effect
      * Medium shot: Movable scene sound
      * Foreground: Interactive scene content
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/044a28560d544771a599a61477bf11ba~tplv-goo7wpa0wc-image.image)
* Spatial scenarios
   * More positional information is required to design sound for object audio (point sound sources).
   * Arrange sound objects according to the different positions within the scene and the sound content.
   * Distinction between the dynamic and static effects of point sound sources.
      * Dynamic sound effects can incorporate randomness, including random playback times, random sound content, and random movement or positioning of the sound.
      * Static sounds, fixed-position sounds.
* Basic sound parameter design for a scene (suggestions for reference)
   * Sampling frequency: 48 kHz
   * Quantization resolution: 24bit
   * Loudness: -13 LUFS
   * Channel selection: mono/stereo
   * Storage format: wav/mp3/ogg
* Design of the sound resources in a scene
   * Define the audio resources required for a scene based on its hierarchy and content.
   * Place sounds based on resource content, with different positions defining different sounds.
   * Sound in immersive scenarios should not easily become excessive or chaotic.
   * As long as the visuals are appropriate and the expression is clear.

#### App applications

* The sound design is also different for different apps.


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.40023474178403756);">

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6e0801efee424708bc6f4917d7ebfaa5~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5997652582159624);margin-left: 16px;">

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c8d04b14ee814d8689118ee350ff9fce~tplv-goo7wpa0wc-image.image)


</div>
</div>


   * Utility class
      * Sound should be simple and lightweight; too many complex sounds can make it appear heavy and cumbersome.
      * Keep the sound duration between 0.5 and 1 second (for reference).
      * Sound types are defined based on UI content, with style attributes for different materials such as none, glass, plastic, and more.
   * Application category
      * Sound can adapt to App attributes, making it more unique and exclusive to the App.
      * The duration of a sound can be defined based on its content, with most durations set between 0.5 and 1.5 seconds as a suggested guideline.
      * The types of sound can be defined based on the application content.
* Sound consistency
   * The definition of basic sounds depends on the overall art design style of the App.
   * Ensure greater consistency in sound across different types of operations.
      * The general sound is the main tone of the App
      * The notification sound is a refinement of the main theme.
      * Custom sound is the soul of the App
* Sound perception
   * A good sound should be clear and free of noise.
   * The prompt tone functionality is accurate.
   * Animation effects and audio blend together naturally without being jarring.
   * Unified audio resource styles

#### Sounds for different usage scenarios
Sounds can be divided into the following categories based on different usage scenarios:

1. **General operations** include interactive events such as: click, long press, drag, double click, pinch to zoom, rotate, and move. Status feedback success/failure; enable/disable
2. **General notifications**, for example: Notification Center - General**,** application notifications, and more
3. **Customized sound**, for example: customized feedback, gesture recognition, power-on, charging, opening the resource library, ringtone for incoming calls, alarm, and so on

#### **General operations**
Perform basic sound design based on different interaction events. The duration can be set to 0.5 to 1.5 seconds according to formatting standards (suggested range). Sound feedback is primarily provided for interactive events. Since these events are routine operations, the sound design follows a light and simple design principle. The same design principles are also applied to status feedback.
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e74e76df3e1d409aa842dd5056fae49f~tplv-goo7wpa0wc-image.image" filename="General - Click.wav" download>General - Click.wav</a>
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/090ed9d3d8dc499790062ba8b55e3e59~tplv-goo7wpa0wc-image.image" filename="General - Feedback.wav" download>General - Feedback.wav</a>
#### General notification
Used for system-level notifications and event reminders. Unlike operation sounds, the configurable duration for notification sounds is 2 to 3.5 seconds (for reference). Notification sounds must be designed to convey reminder information, incorporate inspired and exceptional design concepts, be perceptible to users, and do so without being disruptive. The setting for sound duration should provide users with a brief opportunity to think.
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/86a2965f406646a488d0c15c0a8c9c3b~tplv-goo7wpa0wc-image.image" filename="Notification - Demo 01.wav" download>Notification - Demo 01.wav</a>
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4f9e29a4daf343768480d8da16466502~tplv-goo7wpa0wc-image.image" filename="Notification - Demo 02.wav" download>Notification - Demo 02.wav</a>
#### Custom tone
Each custom sound requires individual design, resulting in greater diversity and recognizability. It is recommended that the design duration for this type of sound be 2 to 3.5 seconds. Customized sounds require more expressive elements, and adding more elements makes the sound richer, such as the opening and closing of the resource library.
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b126ea603cc149b2b9644ffb4d8ec6b4~tplv-goo7wpa0wc-image.image" filename="OpenResourceLibrary.wav" download>OpenResourceLibrary.wav</a>
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b4cdcb9306f148a783502266b69e9c3d~tplv-goo7wpa0wc-image.image" filename="CloseResourceLibrary.wav" download>CloseResourceLibrary.wav</a>
### Sound design principles
Good sound design requires us to listen with our ears, and different approaches to sound design bring me different experiences. We experience different sound experiences from some of the following audio content.
#### Definition of sound properties

1. A light and simple design: Sound should be light and simple, not heavy, highly functional, and avoid a strong sense of impact, conveying ease and comfort.
2. A warm design: The sound should have warm and approachable qualities to make users feel comfortable and friendly.
3. A natural design: Sound should be natural, creating a sense of realism centered on the user and avoiding excessive artificiality or mechanical sounds.

#### Lightweight and simple
According to the definition of the properties of sound, a light and simple sound can provide users with a fast and convenient user experience, making the sound seem more convenient and improving work efficiency.
##### **Positive feedback**
What we want to hear:
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/98522989d49848c9b614db2b735feb1f~tplv-goo7wpa0wc-image.image" filename="Simple and Clean 01.wav" download>Simple and Clean 01.wav</a>
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5bc0687d5a86498aa8466e575a34a464~tplv-goo7wpa0wc-image.image" filename="Simple and Clean 02.wav" download>Simple and Clean 02.wav</a>
##### **Undesired sound**
Things we do not want to hear:
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/259292611aa440b4b968094083dd118c~tplv-goo7wpa0wc-image.image" filename="Undesired Sound 01.wav" download>Undesired Sound 01.wav</a>
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/637fda607386431fa02e0ce4303cb517~tplv-goo7wpa0wc-image.image" filename="Undesired Sound 02.wav" download>Undesired Sound 02.wav</a>
#### **Warm**
Based on the properties of sound, a warm voice can make users feel a greater sense of affinity and comfort, which helps enhance friendliness.
##### **Positive feedback**
What we want to hear:
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1eee2427d77845268fb361c3db2535ae~tplv-goo7wpa0wc-image.image" filename="Warm Sound 01.wav" download>Warm Sound 01.wav</a>
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cd486c07f44245b5be26fb300e773fa9~tplv-goo7wpa0wc-image.image" filename="Warm Sound 02.wav" download>Warm Sound 02.wav</a>
**Undesired sound**
Things we do not want to hear:
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e9fb0d9a0b804e42a03d4edd22cc8cde~tplv-goo7wpa0wc-image.image" filename="Undesired Sound 03.wav" download>Undesired Sound 03.wav</a>
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/59591352811240708c6e21b985ff8505~tplv-goo7wpa0wc-image.image" filename="Undesired Sound 04.wav" download>Undesired Sound 04.wav</a>
#### Natural
Restores sounds to their most authentic state and is commonly used for imitative sound effects, such as keyboard noises or various natural sounds. Sounds that are most familiar to us, such as keyboard typing sounds and camera shutter sounds, can certainly be designed using unconventional audio techniques. However, using physical sounds can create a stronger sense of immersion in these basic operations.
##### **Positive feedback**
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4b0deacd893b44f2a105944e4b3a141c~tplv-goo7wpa0wc-image.image" filename="Take Photos.wav" download>Take Photos.wav</a>


