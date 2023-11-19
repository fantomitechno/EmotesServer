/*
 * MIT License
 *
 * Copyright (c) 2023 Simon RENOUX aka fantomitechno
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.renoux.survival1emotes.config;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.loader.api.config.v2.QuiltConfig;

import static dev.renoux.survival1emotes.Emotes.metadata;

public class ModConfig {
    private TrackedValue<ValueList<String>> Emotes;

    private final Config config;
    private static ModConfig SINGLE_INSTANCE = null;

    public ModConfig() {

        config = QuiltConfig.create(metadata.id(), metadata.id(), builder -> {
            builder.field(Emotes = TrackedValue.create(ValueList.create("kappa:KappaTest", "kappa:KappaTest"), "emotes"));
        });

        save();
    }

    public static ModConfig getConfig() {
        if (SINGLE_INSTANCE == null) {
            SINGLE_INSTANCE = new ModConfig();
        }

        return SINGLE_INSTANCE;
    }

    public void save() {
        config.save();
    }

    public static String getPath() {
        return "config/" + metadata.id() + "/";
    }

    public ValueList<String> getEmotes() {
        return Emotes.getRealValue();
    }
}