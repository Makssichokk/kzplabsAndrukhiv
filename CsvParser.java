import java.util.*;
/** Документний CSV-парсер із підтримкою multiline та екранованих лапок. */
public final class CsvParser {
    private CsvParser(){}
    /** Розбирає CSV з комами та лапками. @param text вміст @return записи @throws CsvExportException неправильний формат */
    public static List<List<String>> parse(String text) throws CsvExportException {
        List<List<String>> rows=new ArrayList<>();List<String> row=new ArrayList<>();StringBuilder cell=new StringBuilder();
        boolean quoted=false,closed=false,started=false;
        for(int i=0;i<text.length();i++){
            char c=text.charAt(i);
            if(quoted){if(c=='"'){if(i+1<text.length()&&text.charAt(i+1)=='"'){cell.append('"');i++;}else{quoted=false;closed=true;}}else cell.append(c);}
            else if(c=='"'){if(started||closed)throw new CsvExportException("Лапки в неочікуваному місці");quoted=true;started=true;}
            else if(c==','){row.add(cell.toString());cell.setLength(0);closed=false;started=false;}
            else if(c=='\n'||c=='\r'){
                if(c=='\r'&&i+1<text.length()&&text.charAt(i+1)=='\n')i++;
                row.add(cell.toString());rows.add(List.copyOf(row));row.clear();cell.setLength(0);closed=false;started=false;
            } else {if(closed)throw new CsvExportException("Символ після закритих лапок");cell.append(c);started=true;}
        }
        if(quoted)throw new CsvExportException("Не закрито лапки у CSV");
        if(started||closed||!row.isEmpty()){row.add(cell.toString());rows.add(List.copyOf(row));}
        return rows;
    }
}
