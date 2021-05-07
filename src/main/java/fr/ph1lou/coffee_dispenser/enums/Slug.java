package fr.ph1lou.coffee_dispenser.enums;

public enum Slug {
    S_1GO("s-1vcpu-1gb"),
    C_4GO("c-2"),
    C_8GO("c-4"),
    C_16GO("c-8"),
    C_32GO("c-16"),
    C_64GO("c-32");

    private final String slug;

    Slug(String slug){
        this.slug=slug;
    }

    public String getSlug() {
        return slug;
    }

}
