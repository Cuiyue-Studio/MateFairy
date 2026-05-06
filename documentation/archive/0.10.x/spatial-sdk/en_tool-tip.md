ToolTip is a tooltip modifier provided by Spatial UI that displays additional text, including title and description, when the user hovers. It also supports configuring the display orientation.
The code sample is as follows:
```Kotlin
@Composable
fun Demo(){
    Box(
        modifier = Modifier
            .tooltip(
                text = "title",
                description = "description",
                direction = it Direction
            )
            .background(Color.Green)
            .clickable { }
            .size(80.dp)
    ) 
}
```

