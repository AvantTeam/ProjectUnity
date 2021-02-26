package unity.graphics.g2d;

import arc.assets.*;
import arc.assets.loaders.*;
import arc.assets.loaders.TextureLoader.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.graphics.g2d.TextureAtlas.TextureAtlasData.*;
import arc.struct.*;

@SuppressWarnings("rawtypes")
public class UnityTextureAtlasLoader extends SynchronousAssetLoader<UnityTextureAtlas, UnityTextureAtlasLoader.TextureAtlasParameter> {
    TextureAtlasData data;

    public UnityTextureAtlasLoader(FileHandleResolver resolver){
        super(resolver);
    }

    @Override
    public UnityTextureAtlas load(AssetManager assetManager, String fileName, Fi file, TextureAtlasParameter parameter){
        for(AtlasPage page : data.getPages()){
            page.texture = assetManager.get(page.textureFile.path().replaceAll("\\\\", "/"), Texture.class);
        }

        TextureAtlas atlas = new TextureAtlas(data);
        data = null;
        return (UnityTextureAtlas)atlas;
    }

    @Override
    public Seq<AssetDescriptor> getDependencies(String fileName, Fi atlasFile, TextureAtlasParameter parameter){
        Fi imgDir = atlasFile.parent();

        if(parameter != null)
            data = new TextureAtlasData(atlasFile, imgDir, parameter.flip);
        else{
            data = new TextureAtlasData(atlasFile, imgDir, false);
        }

        Seq<AssetDescriptor> dependencies = new Seq<>();
        for(AtlasPage page : data.getPages()){
            TextureParameter params = new TextureParameter();
            params.format = page.format;
            params.genMipMaps = page.useMipMaps;
            params.minFilter = page.minFilter;
            params.magFilter = page.magFilter;
            dependencies.add(new AssetDescriptor<>(page.textureFile, Texture.class, params));
        }
        return dependencies;
    }

    public static class TextureAtlasParameter extends AssetLoaderParameters<UnityTextureAtlas>{
        /** whether to flip the texture atlas vertically **/
        public boolean flip = false;

        public TextureAtlasParameter(){
        }

        public TextureAtlasParameter(boolean flip){
            this.flip = flip;
        }

        public TextureAtlasParameter(LoadedCallback loadedCallback){
            super(loadedCallback);
        }
    }
}
