package de.zonlykroks.cypher.impl;

import de.zonlykroks.cypher.SupportedCypher;
import de.zonlykroks.gui.encrypt.FileEncryptionModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DukeNukeEm implements SupportedCypher {

    @Override
    public boolean isSymmetricCypher() {
        return false;
    }

    @Override
    public void encrypt(File work, char[] hashedPassword) throws IOException {

        List<String> dataMess = new ArrayList<>();

        for(int i = 0; i < 50; i++) {
            dataMess.add(FileEncryptionModule.genRandom32String());
        }

        Files.write(work.toPath(),dataMess);
        Files.delete(work.toPath());
    }

    @Override
    public void decrypt(File work, char[] hashedPassword){
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "DukeNukeEm (write garbage to file, then delete)";
    }
}
