package com.venned.farmrecipes.file;

import com.venned.farmrecipes.FarmRecipes;

public class DataFile extends AbstractFile {
    public DataFile(FarmRecipes plugin) {
        super(plugin, "data.yml");
    }

    public void incrementPlantAmount(String uuid) {
        String path = uuid + ".amount";
        int currentAmount = this.getFile().getInt(path, 0);
        this.getFile().set(path, currentAmount + 1);
        this.saveFile();
    }
}
