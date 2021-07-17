import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WallaRobot extends BaseRobot {


    public WallaRobot() {
        super("https://www.walla.co.il/");
    }

    @Override
    public Map<String, Integer> getWordsStatistics() {
        Map<String, Integer> wordsMap = new HashMap<>();
        try {
            Document website = Jsoup.connect(getRootWebsiteUrl()).get();
            Elements allLinks = website.getElementsByAttribute("href");
            StringBuilder allText = new StringBuilder();
                    int i = 0;
                    while(i < allLinks.size()) {
                Element linkElement = allLinks.get(i);
                String link = linkElement.attr("href");
                if (i != allLinks.size() - 1) {
                    if (link.contains(".walla.co.il/") && link.contains("news")) {
                        Document wallaPage = Jsoup.connect(link).get();
                        Elements mainTitle = wallaPage.getElementsByTag("h1");
                        Elements textPage = wallaPage.getElementsByTag("p");
                        allText.append(mainTitle.text()).append(" ");
                        for (Element element : textPage) {
                            Element containerText = element.parent().parent();
                            String classValue = containerText.attr("class");
                            Element containerSubTitle = element.parent();
                            String tagName = containerSubTitle.tagName();
                            if (tagName.equals("header")) {
                                allText.append(element.text()).append(" ");
                            }
                            if (classValue.equals("css-onxvt4  ")) {
                                allText.append(element.text()).append(" ");
                            }
                        }
                    }
                }
                i++;
            }
            wordsMap = setMap(allText);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordsMap;
    }

    public Map<String, Integer> setMap (StringBuilder allText){
        Map<String, Integer> wordsMap = new HashMap<>();
        String word = "";
        for (int i = 0; i<allText.length(); i++){
            char checkWord = allText.charAt(i);
            if ((checkWord >= 'א' && checkWord <= 'ת') || checkWord == '"' ||
                    (checkWord >= '0' && checkWord <= '9') || (checkWord >= 'A' && checkWord <= 'Z') || (checkWord >= 'a' && checkWord <= 'z')){
                if (!(checkWord == '"')) {
                    word += checkWord;
                }
            }
            else {
                if (word.length() != Definitions.MINIMUM_WORD) {
                    if (wordsMap.get(word) != null) {
                        Integer value = wordsMap.get(word) + Definitions.ONE_APPEARANCE;
                        wordsMap.put(word, value);
                    }
                    else {
                        wordsMap.put(word, Definitions.ONE_APPEARANCE);
                    }
                }
                word = "";
            }
        }
        return wordsMap;
    }

    @Override
    public int countInArticlesTitles(String text) {
        int count = 0;
        StringBuilder allTitlesText = new StringBuilder();
        try {
            Document website = Jsoup.connect(getRootWebsiteUrl()).get();
            Elements allLinks = website.getElementsByAttribute("href");

            for (int i = 0; i < allLinks.size(); i++){
                Element linkElement = allLinks.get(i);
                String link = linkElement.attr("href");
                if (i != allLinks.size() - 1) {
                    if (link.contains(".walla.co.il/") && !link.equals(allLinks.get(i + 1).attr("href")) && scanLink(link) > Definitions.WALLA_PAGE
                    && link.contains("news")){
                        Document wallaPage = Jsoup.connect(link).get();
                        Elements mainTitle = wallaPage.getElementsByTag("h1");
                        Elements textPage = wallaPage.getElementsByTag("p");
                        allTitlesText.append(mainTitle.text()).append(" ");
                        for (Element element : textPage) {
                            Element containerSubTitle = element.parent();
                            String tagName = containerSubTitle.tagName();
                            if (tagName.equals("header")) {
                                allTitlesText.append(element.text()).append(" ");
                            }
                        }
                    }
                }
            }
            count = countAppearance(allTitlesText, text);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }

    public int countAppearance (StringBuilder allTitlesText, String text) {
        int count = 0;
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < allTitlesText.length(); i++){
            char checkWord = allTitlesText.charAt(i);
            if ((checkWord >= 'א' && checkWord <= 'ת') || checkWord == '"' || checkWord == ' ' ||
                    (checkWord >= '0' && checkWord <= '9') || (checkWord >= 'A' && checkWord <= 'Z') || (checkWord >= 'a' && checkWord <= 'z')){
                if (!(checkWord == '"')) {
                    word.append(checkWord);
                    if (word.toString().contains(text)){
                        count++;
                        word = new StringBuilder();
                    }
                }
            }
        }

        return count;
    }

    @Override
    public String getLongestArticleTitle() {
        String longestArticleTitle = "";
        try {
            Document website = Jsoup.connect(getRootWebsiteUrl()).get();
            Elements allLinks = website.getElementsByAttribute("href");

            int previousLength = 0;
            int currentLength;
            String linkOfLongestArticle = "";
            for (int i = 0; i < allLinks.size(); i++) {
                StringBuilder allText = new StringBuilder();
                Element linkElement = allLinks.get(i);
                String link = linkElement.attr("href");
                if (i != allLinks.size() - 1) {
                    if (link.contains(".walla.co.il/") && !link.equals(allLinks.get(i + 1).attr("href")) && scanLink(link) > Definitions.WALLA_PAGE
                            && link.contains("news")){
                        Document wallaPage = Jsoup.connect(link).get();
                        Elements textPage = wallaPage.getElementsByTag("p");
                        for (Element element : textPage) {
                            Element containerText = element.parent().parent();
                            String classValue = containerText.attr("class");
                            if (classValue.equals("css-onxvt4  ")) {
                                allText.append(element.text()).append(" ");
                            }
                        }
                        currentLength = allText.length();
                        if (currentLength > previousLength) {
                            linkOfLongestArticle = link;
                            previousLength = currentLength;
                        }
                    }
                }
            }
            Document wallaPage = Jsoup.connect(linkOfLongestArticle).get();
            Elements mainTitle = wallaPage.getElementsByTag("h1");
            longestArticleTitle = mainTitle.text();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return longestArticleTitle;
    }

    public int scanLink (String linkToScan){
        int count = 0;
        for (int i = 0; i < linkToScan.length(); i++){
            char check = linkToScan.charAt(i);
            if (check == '/'){
                count++;
            }
        }
        return count;
    }
}
