import json
import struct

def parse_glb(file_path):
    with open(file_path, 'rb') as f:
        header = f.read(12)
        magic, version, length = struct.unpack('<4sII', header)
        
        chunk_header = f.read(8)
        chunk_length, chunk_type = struct.unpack('<II', chunk_header)
        
        if chunk_type == 0x4E4F534A: # 'JSON'
            json_data = f.read(chunk_length).decode('utf-8')
            gltf = json.loads(json_data)
            
            nodes = gltf.get('nodes', [])
            for i, anim in enumerate(gltf.get('animations', [])):
                print(f"Animation {i}: {anim.get('name', 'unnamed')}")
                target_nodes = set()
                scale_target_nodes = set()
                for channel in anim.get('channels', []):
                    target_node_idx = channel.get('target', {}).get('node')
                    path = channel.get('target', {}).get('path')
                    if target_node_idx is not None:
                        node_name = nodes[target_node_idx].get('name', str(target_node_idx))
                        target_nodes.add(node_name)
                        if path == 'scale':
                            scale_target_nodes.add(node_name)
                print(f"  Targets scale on: {scale_target_nodes}")

parse_glb('app/src/main/assets/pico_robot_animated.glb')
