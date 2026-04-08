package com.palsandpalms.model;

import java.util.Objects;

public final class ResidentAppearance {
    private String name;
    private Gender gender;
    private HairColor hairColor;
    private HairLength hairLength;
    private EyeColor eyeColor;
    private SkinTone skinTone;

    public ResidentAppearance() {
    }

    public ResidentAppearance(String name, Gender gender, HairColor hairColor, HairLength hairLength,
                              EyeColor eyeColor, SkinTone skinTone) {
        this.name = Objects.requireNonNull(name);
        this.gender = Objects.requireNonNull(gender);
        this.hairColor = Objects.requireNonNull(hairColor);
        this.hairLength = Objects.requireNonNull(hairLength);
        this.eyeColor = Objects.requireNonNull(eyeColor);
        this.skinTone = Objects.requireNonNull(skinTone);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public HairColor getHairColor() {
        return hairColor;
    }

    public void setHairColor(HairColor hairColor) {
        this.hairColor = hairColor;
    }

    public HairLength getHairLength() {
        return hairLength;
    }

    public void setHairLength(HairLength hairLength) {
        this.hairLength = hairLength;
    }

    public EyeColor getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(EyeColor eyeColor) {
        this.eyeColor = eyeColor;
    }

    public SkinTone getSkinTone() {
        return skinTone;
    }

    public void setSkinTone(SkinTone skinTone) {
        this.skinTone = skinTone;
    }

    public ResidentAppearance copy() {
        return new ResidentAppearance(name, gender, hairColor, hairLength, eyeColor, skinTone);
    }
}
