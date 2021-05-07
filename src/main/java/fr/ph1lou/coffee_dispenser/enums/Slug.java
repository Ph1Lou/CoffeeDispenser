package fr.ph1lou.coffee_dispenser.enums;

public enum Slug {
    S_1GO("s-1vcpu-1gb",900),
    C_4GO("c-2",4000),
    C_8GO("c-4",8100),
    C_16GO("c-8",16200),
    C_32GO("c-16",32400),
    C_64GO("c-32",64800);

    private final String slug;
    private final int ram;

    Slug(String slug,int ram){
        this.slug=slug;
        this.ram=ram;
    }

    public String getSlug() {
        return slug;
    }

    public int getRam() {
        return ram;
    }
}
