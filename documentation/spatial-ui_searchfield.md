SearchField is a component, defined under the PICO design guidelines, that allows users to enter text and initiate a search by pressing the search button on the keyboard or by other means.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/378842a932a14c16b314a9c81e0d5e90~tplv-goo7wpa0wc-image.image)
## API Surface

* `value`: The current text value in the SearchField.
* `onValueChange`: Callback function invoked when the text in SearchField changes. It takes a new text value as a parameter.
* `onSearch`: Callback triggered when the user clicks the search button on the software keyboard.
* `placeholder`: The content displayed in the search box when it is empty, typically text.
* `leadingContent`: Used to display custom content at the beginning of the search box. The default search icon provided by `SearchFieldDefaults.searchIcon`.
* `enabled`: Boolean value indicating whether the search box is enabled. When set to false, the search box is neither editable nor focusable.
* `textStyle`: The text style applied to input text in the search box. By default, the style defined in `SearchFieldDefaults.DefaultTextStyle` is used.
* `interactionSource`: Represents the `MutableInteractionSource` of this SearchField's interaction flow. A custom `MutableInteractionSource` can be provided to observe the interaction behavior of SearchField.
* `cornerRadius`: The corner radius of the search box. The default is 100.dp.
* colors: Sets the color values for the search box, including background color, text color, placeholder color, and more. By default, the colors are provided by `SearchFieldDefaults.searchFieldColors`.

## Basic usage
```Java
@Composable
fun SimpleSearchFieldSample() {
    var value by remember { mutableStateOf("") }
    var searchValue by remember { mutableStateOf("") }
    Column {
        SearchField(
            value = value,
            onValueChange = { value = it },
            onSearch = {
                searchValue = value
            },
        )
        Text("searchFor: $searchValue")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a35ee464007846f09da735dd48e49d9f~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## **Advanced usage**
You can achieve custom icon modification by customizing the `placeholder` and `leadingContent` in SearchField.
```Java
@Composable
fun SimpleSearchFieldSample() {
    var value by remember { mutableStateOf("") }
    var searchValue by remember { mutableStateOf("") }
    Column {
        SearchField(
            value = value,
            onValueChange = { value = it },
            placeholder = { Text(text = "Search") },
            onSearch = {
                searchValue = value
            },
            leadingContent = { Icon(painter = painterResource(R.drawable.ic_sample_voice), null)
            },
            colors = SearchFieldDefaults.searchFieldColors(textColor = Color.Red, backgroundColor = Color.Black, focusedColor = Color.Black, placeholderColor = Color.White)
        )
        Text("searchFor: $searchValue")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/150acdea406a4e0eb3ddd03b96e0b324~tplv-goo7wpa0wc-image.image" width="1280px" /></div>



