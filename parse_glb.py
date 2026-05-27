import json
import struct
import sys

def parse_glb(file_path):
    with open(file_path, 'rb') as f:
        magic = f.read(4)
        if magic != b'glTF':
            print(f"Error: {file_path} is not a valid GLB file.")
            return

        version = struct.unpack('<I', f.read(4))[0]
        length = struct.unpack('<I', f.read(4))[0]

        print(f"GLB Version: {version}")
        print(f"Total Length: {length} bytes")

        # Read the first chunk (JSON)
        chunk_length = struct.unpack('<I', f.read(4))[0]
        chunk_type = f.read(4)

        if chunk_type != b'JSON':
            print("Error: First chunk is not JSON.")
            return

        json_data = f.read(chunk_length).decode('utf-8')
        gltf = json.loads(json_data)

        # Basic Info
        print("\n--- GLTF Info ---")
        if 'asset' in gltf:
            print(f"Generator: {gltf['asset'].get('generator', 'Unknown')}")
        
        # Check Meshes
        meshes = gltf.get('meshes', [])
        print(f"\nTotal Meshes: {len(meshes)}")
        
        # Check Nodes
        nodes = gltf.get('nodes', [])
        print(f"Total Nodes: {len(nodes)}")

        # Check Animations
        animations = gltf.get('animations', [])
        print(f"\nTotal Animations: {len(animations)}")
        for i, anim in enumerate(animations):
            name = anim.get('name', f"Unnamed_Animation_{i}")
            channels = len(anim.get('channels', []))
            samplers = len(anim.get('samplers', []))
            print(f"  [{i}] {name} (Channels: {channels}, Samplers: {samplers})")

if __name__ == "__main__":
    if len(sys.argv) > 1:
        parse_glb(sys.argv[1])
    else:
        print("Please provide a path to a GLB file.")
