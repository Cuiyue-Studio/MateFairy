You can use the `context.getSelfSpaceState()` method to obtain an app's current space state. This method returns the enumeration type `SpaceState`, including the following values: `SpaceState.UNKNOWN`, `SpaceState.SHARED_SPACE`, and `SpaceState.FULL_SPACE`.
```Kotlin
@Composable
fun SpaceStateExample() {
    val context = LocalContext.current
    var spaceState: String by remember { mutableStateOf(context.getSelfSpaceState().toString()) }
    Column {
        Text(text = "Current space state = $spaceState")
        Button(onClick = { spaceState = context.getSelfSpaceState().toString() }) {
            Text(text = "Get space state")
        }
    }
}
```

To ensure that your app runs only in Full Space state, you can call `context.enforceSelfFullSpace()` to verify. This method verifies the app's current space state. If it is not in Full Space, it throws `IllegalStateException`.

