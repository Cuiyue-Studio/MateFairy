#!/bin/bash
java -cp build/classes/kotlin/main:$(find ~/.gradle/caches -name "*.jar" | tr '\n' ':') com.example.test.Test_navigatorKt
