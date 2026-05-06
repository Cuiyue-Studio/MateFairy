To facilitate providing the complete official PICO documentation to large language models (LLMs), we offer a set of AI-optimized, streamlined Markdown documents. Compared to HTML, Markdown has a clearer structure and is more suitable for model parsing; it also eliminates the need to use scripts to scrape web content, which can save time and token costs to some extent.
## llms.txt URLs

* **Chinese Mainland**
   * Chinese version: [https://developer-cn.picoxr.com/llmstxt/document/spatial/zh/llms.txt](https://developer-cn.picoxr.com/llmstxt/document/spatial/zh/llms.txt)
   * English version: [https://developer-cn.picoxr.com/llmstxt/document/spatial/en/llms.txt](https://developer-cn.picoxr.com/llmstxt/document/spatial/en/llms.txt)
* **Outside Chinese Mainland**
   * Chinese version: [https://developer.picoxr.com/llmstxt/document/spatial/zh/llms.txt](https://developer.picoxr.com/llmstxt/document/spatial/zh/llms.txt)
   * English version: [https://developer.picoxr.com/llmstxt/document/spatial/en/llms.txt](https://developer.picoxr.com/llmstxt/document/spatial/en/llms.txt)

## Usage recommendations
When providing context to large language models, it is recommended to directly provide the URL of the `llms.txt` file.
The advantage of this approach is that the model can automatically retrieve all `.md` documents referenced by this index, thereby obtaining more complete and up-to-date official information. Compared to supplying individual `.md` pages one by one or parsing HTML pages with redundant structures and distracting elements, this method offers superior coverage, stability, and efficiency.
To provide only the Markdown content of a single document to AI, use the following format:
```XML
/llmstxt/document/spatial/{lang}/{page-slug}.md
```


