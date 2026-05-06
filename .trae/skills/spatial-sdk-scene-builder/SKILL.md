---
name: spatial-sdk-scene-builder
description: Orchestrates the calculation and structuring of 3D entity transformations (Position, Rotation, Scale) for spatial environments, enforcing human-scale realism.
tools:
  - name: calculate_bbox
    description: Python script to extract bounding box dimensions from .glb, .gltf, or .usdz files.
    command: python3 ./skills/spatial-sdk-scene-builder/scripts/calculate_bbox.py {{file_path}}
metadata:
  author: Ye Nan, Jin Yan
---

# Building 3D Scenes for Spatial Apps

## Overview
This skill guides the AI in assisting developers with the spatial layout of 3D models and spatial UI panels. It ensures physical dimensions are accurately mapped into the application's coordinate system to maintain realistic, human-scale proportions and an optimized developer experience.

## When to use this skill
Trigger this skill when a user is positioning 3D assets, transitioning UI elements into a spatial context, or establishing the initial scene hierarchy and layout in a spatial computing environment.

## Available Command Tools
- `calculate_bbox`: **MANDATORY COMMAND TOOL.** This is the ONLY source of truth for asset dimensions. You must use the tool named `calculate_bbox` (which maps to `calculate_bbox.py {{file_path}}`) to get the dimensions.
  - *Dependency Note:* This script requires dependencies listed in `requirements.txt`. If the script fails due to missing dependencies, you must manually run `pip3 install -r ./skills/spatial-sdk-scene-builder/scripts/requirements.txt` via Bash before retrying.

## Step 1: Contextual Reasoning & Common Sense Estimation
Before processing any numbers, analyze the intended scene.
- Based on your general knowledge, formulate a clear description of what this scene represents (e.g., "A standard office desk setup," "A grassy outdoor pasture").
- Estimate the logical, real-world dimensions of this overall scene and the expected size of typical elements within it.
- **Constraint:** All estimations must align with human common sense and realistic physical proportions.

## Step 2: MANDATORY Dimension Collection (The "No-Guessing" Rule)
**You are FORBIDDEN from guessing or estimating the raw dimensions of a 3D file.**
1. Identify all 3D model files in the request or directory.
2. **Implicit Trigger:** Even if not explicitly asked, you MUST run the `calculate_bbox` tool (with `file_path="<absolute_path>"`) for every identified asset before proceeding to Step 3.
3. **Path Resolution:** Convert to **Absolute Paths**.
4. **Unit Conversion (CRITICAL):** 
   - `.usdz` output: Divide by 100 (cm to m).
   - `.glb/.gltf` output: Use as is (m).
   
**If the tool call fails or is skipped, you must stop and inform the user that spatial layout cannot be calculated without precise bounding box data.**

## Step 3: Transform Calculation
Based on the collected dimensions and your common sense estimation from Step 1, compute the spatial transform for each entity. Consider the scene graph hierarchy (parent-child node relationships).
- **Scale:** Calculate the required scale factor (the ratio of the intended scene dimension to the raw 3D model's native dimension). **Crucial:** Ensure the final scaled size of the element aligns strictly with realistic human perception (e.g., a virtual chair should be scaled to match a real-world chair).
- **Translation / Position:** Determine the X, Y, Z coordinates relative to the world origin or the designated parent node.
- **Rotation:** Define the orientation. Default to Euler angles for readability, but calculate Quaternions if mathematically necessary for the SDK.

## Step 4: Output Generation and File Saving
- Format the scene data into a structured **JSON format**.
- The JSON output MUST strictly include the following hierarchy:
    1. **Scene Context:** A text description of the scene and the AI's logical reasoning for the estimated dimensions.
    2. **Scene Dimensions:** The overall dimensions of the entire 3D scene (in meters).
    3. **Elements Array:** A list of all entities in the scene, where each element explicitly contains:
        - **Original Bounding Box:** The raw, unmodified dimensions of the element (in meters).
        - **Transformation:** The final calculated Translation, Rotation, and Scale for the element.
- **Output Requirement:** You must save this generated JSON configuration directly to a file within the `.spatialsdk/` directory at the root of the project (e.g., `.spatialsdk/scene_transforms.json`).
