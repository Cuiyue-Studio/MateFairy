## Typesetting principles
### Ensure text is clear and easy to read
Text is one of the most fundamental elements of application content, and ensuring that text is clear and easy to read is essential for user experiences such as reading, immersion, physiological comfort, and other experiences. Especially at present, XR device displays have not yet reached retina-level quality, and visible pixelation reduces the overall display performance. In actual use, users may be in dynamic and complex three-dimensional VST or virtual environments, so text design must pay special attention to users' reading experience. Pay special attention to the following areas when designing the layout:

* **Font size**
   The platform recommends using 12 dp as the minimum font size to ensure that most users, under different visual conditions, can read easily and use the platform for extended periods without experiencing visual fatigue. If you use a font size smaller than 12 dp, you must ensure that it is only applied to auxiliary or non-essential information that is not interactive, strictly limit its usage, and test to confirm that it does not impede overall readability or user experience.
* **Font weight**
   When using languages with complex character shapes such as CJK (Chinese, Japanese, and Korean) and the font size is 17 sp or smaller, it is recommended to use Medium font weight as the default for body text. Using Bold font weight for bolding is not recommended, as it may cause strokes to appear unclear, merge together, or flicker, leading to a degraded reading experience. When displaying in Western scripts or languages with simple character shapes, the requirements may be slightly relaxed. Adjustments can also be made as appropriate when using a bolder font.
* **Character spacing**
   On non-retina displays, applying different letter spacing to text of varying font sizes can effectively improve the reading experience and add greater visual hierarchy to the layout. At the same time, it also improves the reading experience of text when multiple windows are displayed side by side, whether viewed from a non-frontal angle or in distant windows.
* **Font**
   The official PICO font (see the font resources section below) has been specially optimized for display and reading experience on XR devices, and can meet the requirements of most applications. Of course, fonts are not only carriers of information, but also a direct reflection of application style and brand tone. When selecting custom fonts to further support brand storytelling, in addition to considering how well the font matches the brand’s tone, it is important to prioritize sans-serif fonts with open counters and simple strokes. This ensures that text remains legible on non-retina screens and at various viewing distances and angles.
* **Character width**
   Character width has a significant impact on multilingual adaptation and the overall quality and tone. The official PICO font supports continuous adjustment and display of character width (see the font resources section below). When using the character width feature, it is recommended to keep character width within a reasonable range. Excessive compression or stretching of character width can seriously diminish the reading experience. Thorough testing is required to ensure a proper balance between functionality and aesthetics.
* **Line height**
   Apply appropriate line height to text in different usage scenarios to ensure a comfortable reading experience while enhancing the rhythm of the layout. If the text does not need to wrap or only needs to be displayed in two lines, and a multi-line reading experience is not important, it is recommended to use a line height of 125%. When text is displayed in three or more lines, and when it is necessary to emphasize the multi-line reading experience, it is recommended to use a line height of 150% or 175%. When the primary function of the application is related to reading, the line height standard can be further relaxed.
* **Colors and contrast**
   The platform recommends using blending mode for all text colors to ensure sufficient contrast between text and background, thereby guaranteeing a basic reading experience. When text is displayed directly in VST or virtual environments, or on complex and variable texture backgrounds, try to use larger font sizes and bold text. At the same time, consider adding a colored overlay to the background or applying Gaussian blur to reduce interference from background textures.
   Additionally, when text is displayed on the system's default material background, use colored fills with caution due to the diversity of background colors. If you need to use it, be sure to test whether the contrast meets the standard in multiple environments.
* **3D text**
   In most information delivery scenarios, 2D text can provide optimal readability and performance. However, 3D text can be used in scenarios where it is necessary to emphasize spatial awareness, immersion, or strong relevance to the content. Spatial UI supports a wide range of 3D text styles, such as adding thickness, texture, lighting, and more to text. When using these styles, avoid excessive decoration that distracts users, thus affecting the text reading experience.

### Clearly convey the information structure
In XR scenarios, users' attention is easily distracted by dynamic and complex three-dimensional VST or virtual environments. Therefore, the information structure must be clear and well-defined to help users quickly capture key content and guide them along an appropriate reading flow.

* **Unified information structure styles**
   By using text styles such as font size, font weight, color, line height, and letter spacing, you can clearly distinguish headings, body text, and supplementary information. This establishes a clear hierarchy, allowing users to immediately grasp the structure of the content and key information, thereby improving content comprehensibility as well as the reach and efficiency of important information.
* **An appropriate number of information structures**
   Limit the number of information structure styles (see the **Text guidelines** section below) to reduce user cognitive load and maintain content consistency.

### Responsive adaptations
Most XR applications support free window resizing. If your application also plans to support this feature, make sure to define the upper and lower limits for screen size, and establish clear rules for responsive adaptation. At the same time, it is important to note that when the window becomes larger, the text display width should be controlled so that each line does not exceed 50 Chinese characters. A moderate text width helps maintain a stable reading rhythm and prevents eye movement burden or interruptions in reading flow.
## Font resources
PICO officially provides the six fonts, five of which support continuously variable weight and width, offering greater flexibility for multilingual adaptation, typesetting, and quality creation. Font files are built into PICO OS 6, so developers can use them directly in their applications. For more information, refer to "[Font](/font-resource)".
## Text guidelines
To create a unified information structure and hierarchy, and to ensure a good reading experience for text in different scenarios. The platform provides five unified text styles: Display, Headline, Title, Label, and Body. Each style is further subdivided by text size and line height to meet the layout requirements of different scenarios.
### Display
Display is the text style with the largest font size and the strongest visual impact in the typography specification, and includes three sizes: Large, Medium, and Small. The primary purposes are to convey brand identity, emphasize key information, serve as a visual anchor, among others. It is typically used for short, important text, such as window titles, page titles, and more.
| **Design_tokens** | **Font weight \| Font size \| Line height \| Letter spacing** |
| --- | --- |
| Display/Large | Bold \| 40sp \| 54sp \| -1.18px |
| Display/Medium | Bold \| 32sp \| 40sp \| -0.96px |
| Display/Small | Bold \| 28sp \| 34sp \| -0.84px |
### Headline
Headline is the text style in the typography specification with a font size second only to Display, and it is available in Large, Medium, and Small sizes. The primary purpose is to guide users to quickly locate content hierarchy or key information, serving functions such as layering and grouping content, and more. It is generally used for short, important text, such as pop-up titles, module titles, and more.
| **Design_tokens** | **Font weight \| Font size \| Line height \| Letter spacing** |
| --- | --- |
| Headline/Large | Bold \| 24sp \| 30sp \| -0.48px |
| Headline/Medium | Bold \| 20sp \| 26sp \| 0.24px |
| Headline/Small | Bold \| 16sp \| 20sp \| 0.40px |
### Title
Title is the weakest heading style in the text specification and includes three sizes: Large, Medium, and Small. Primarily used for titles of smaller modules or content, with a certain degree of distinction from both body text and Headline.
| **Design_tokens** | **Font weight \| Font size \| Line height \| Letter spacing** |
| --- | --- |
| Title/Large | Semibold \| 20sp \| 26sp \| 0.12px |
| Title/Medium | Semibold \| 16sp \| 20sp \| 0.32px |
| Title/Small | Semibold \| 14sp \| 18sp \| 0.28px |
### Label
Label includes three sizes: Large, Medium, and Small. Mainly used for text with a small font size that is functionally important, such as buttons, labels, and similar elements.
| **Design_tokens** | **Font weight \| Font size \| Line height \| Letter spacing** |
| --- | --- |
| Label/Large | Semibold \| 14sp \| 18sp \| 0.28px |
| Label/Medium | Semibold \| 12sp \| 16sp \| 0.24px |
| Label/Small | Semibold \| 11sp \| 14sp \| 0.22px |
### Body
Body is the main text style, and includes four sizes—Large, Medium, Small, and Tiny—as well as two line height styles: Default and Multiline. The primary purpose is for displaying unimportant, long-form text over a large area, such as body text, secondary information, and so on.
| **Design_tokens** | **Font weight \| Font size \| Line height \| Letter spacing** |
| --- | --- |
| Body/Large Default | Medium \| 16sp \| 20sp \| 0.30px |
| Body/Large Multiline | Medium \| 16sp \| 24sp \| 0.30px |
| Body/Medium Default | Medium \| 14sp \| 18sp \| 0.26px |
| Body/Medium Multiline | Medium \| 14sp \| 22sp \| 0.26px |
| Body/Small | Medium \| 12sp \| 16sp \| 0.22px |
| Body/Tiny | Medium \| 10sp \| 12sp \| 0.18px |
