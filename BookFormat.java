/** Фіксовані формати книжок. */
public enum BookFormat {
    /** Паперова книжка. */ PRINTED("друкована"),
    /** Електронна книжка. */ ELECTRONIC("електронна");
    private final String label;
    BookFormat(String label){this.label=label;}
    /** Повертає українську назву формату. */ public String label(){return label;}
}
