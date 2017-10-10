package model;

public class LexicoToken {
    private String type;
    private String lexema;
    private String row;
    private String column;

    public LexicoToken(String row, String column, String type, String lexema) {
        this.type = type;
        this.lexema = lexema;
        this.row = row;
        this.column = column;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type='" + type + '\'' +
                ", lexema='" + lexema + '\'' +
                ", row='" + row + '\'' +
                ", column='" + column + '\'' +
                '}';
    }
}
