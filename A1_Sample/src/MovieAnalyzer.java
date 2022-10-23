import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;


/**
 * @author 11710301
 */
public class MovieAnalyzer {
  ArrayList<Movie> movies = new ArrayList<>();

  class Movie {
    String Series_Title;
    int Release_Year;
    String Certificate;
    String Runtime;
    String Genre;
    String IMDB_Rating;
    String Overview;
    String Meta_score;
    String Director;
    String Star1;
    String Star2;
    String Star3;
    String Star4;
    int votes;
    String Gross;

    Movie(String Series_Title, int Release_Year, String Certificate, String Runtime, String Genre, String IMDB_Rating, String Overview, String Meta_score, String Director, String Star1, String Star2, String Star3, String Star4, int votes, String Gross) {
      this.Series_Title = Series_Title;
      this.Release_Year = Release_Year;
      this.Certificate = Certificate;
      this.Runtime = Runtime;
      this.Genre = Genre;
      this.IMDB_Rating = IMDB_Rating;
      this.Overview = Overview;
      this.Meta_score = Meta_score;
      this.Director = Director;
      this.Star1 = Star1;
      this.Star2 = Star2;
      this.Star3 = Star3;
      this.Star4 = Star4;
      this.votes = votes;
      this.Gross = Gross;
    }


}
  /**
   * @author 11710301
   */

  @SuppressWarnings("checkstyle:LocalVariableName")
  public MovieAnalyzer(String dataset_path) {

    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataset_path), "UTF-8"));
      reader.readLine();
      String line = null;
      while ((line = reader.readLine()) != null) {
        String str;

        line += ",";
        Pattern pCells = Pattern
                .compile("(\"[^\"]*(\"{2})*[^\"]*\")*[^,]*,");
        Matcher mCells = pCells.matcher(line);
        List<String> cells = new LinkedList();

        while (mCells.find()) {
          str = mCells.group();
          str = str.replaceAll("\"\"", "&&&");
          str = str.replaceAll(
                "(?sm)\"?([^\"]*(\"{2})*[^\"]*)\"?.*,", "$1");
          str = str.replaceAll("&&&", "\"\"");
          cells.add(str);
        }

        Movie movie = new Movie(cells.get(1), Integer.parseInt(cells.get(2)), cells.get(3),
                cells.get(4), cells.get(5),
                cells.get(6), cells.get(7), cells.get(8), cells.get(9), cells.get(10),
                cells.get(11), cells.get(12), cells.get(13), Integer.parseInt(cells.get(14)), cells.get(15));
        movies.add(movie);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
    /**
     * @author 11710301
     */

  public Map<Integer, Integer> getMovieCountByYear() {
    Map<Integer, Integer> map = new TreeMap<Integer, Integer>(
            new Comparator<Integer>() {
            @Override
                public int compare(Integer o1, Integer o2) {

                return (int) (o2 - o1);
            }
            });


    for (int i = 0; i < movies.size(); i++) {
      int tempYear = movies.get(i).Release_Year;
      if (map.get(tempYear) == null) {
        map.put(tempYear, 1);
      } else {
        map.put(tempYear, map.get(tempYear) + 1);
      }
    }
    return map;
  }
  /**
   * @author 11710301
   */

  public Map<String, Integer> getMovieCountByGenre() {
    Map<String, Integer> map = new TreeMap<String, Integer>();
    for (Movie movie : movies) {
      String[] genre = movie.Genre.split(",");
      for (int j = 0; j < genre.length; j++) {
        genre[j] = genre[j].trim();
        if (map.get(genre[j]) == null) {
          map.put(genre[j], 1);
        } else {
              map.put(genre[j], map.get(genre[j]) + 1);
        }
      }
    }
    Map<String, Integer> sorted = map
            .entrySet()
            .stream()
            .sorted(Collections.reverseOrder(comparingByValue()))
            .collect(
                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                            LinkedHashMap::new));
    return sorted;

  }
  /**
   * @author 11710301
   */

  @SuppressWarnings("checkstyle:Indentation")
  public Map<List<String>, Integer> getCoStarCount() {
    Map<List<String>, Integer> map = new HashMap<>();
      for (Movie movie : movies) {
      String[] stars = new String[4];
      stars[0] = movie.Star1.trim();
      stars[1] = movie.Star2.trim();
      stars[2] = movie.Star3.trim();
      stars[3] = movie.Star4.trim();
      String temp;
      for (int j = 0; j < 4; j++) {
        for (int k = j + 1; k < 4; k++) {
          if (stars[j].compareTo(stars[k]) >= 0) {
            temp = stars[j];
            stars[j] = stars[k];
            stars[k] = temp;
          }
        }
      }
      for (int j = 0; j < 4; j++) {
        for (int k = j + 1; k < 4; k++) {
          List<String> tempList = new ArrayList<>();
          tempList.add(stars[j].trim());
          tempList.add(stars[k].trim());
          Iterator<List<String>> iterator = map.keySet().iterator();
          boolean notInmap = true;
          while (iterator.hasNext()) {
            List<String> temp_list = iterator.next();
            if (temp_list.get(0).equals(tempList.get(0)) && temp_list.get(1).equals(tempList.get(1))) {
              map.put(temp_list, map.get(temp_list) + 1);
              notInmap = false;
            }

          }
          if (notInmap) {
              map.put(tempList, 1);
          }
      }
    }

      }

        return map;
    }

  /**
   * @author 11710301
   * */

  public List<String> getTopMovies(int top_k, String by) {
    List<String> list = new ArrayList<>();
    if (by.equals("runtime")) {
      for (int i = 0; i < movies.size(); i++) {
        for (int j = i + 1; j < movies.size(); j++) {
          if(runTimeBigger(movies.get(j), movies.get(i))) {
            Collections.swap(movies, i, j);
          }
        }
      }
      for (int i = 0; i < top_k; i++) {
        list.add(movies.get(i).Series_Title);
      }
    } else {
      for (int i = 0; i < movies.size(); i++) {
        for (int j = i + 1; j < movies.size(); j++) {
          if(overviewBigger(movies.get(j),movies.get(i))) {
            Collections.swap(movies,i,j);
          }
        }
      }
      for (int i = 0; i < top_k; i++) {
        list.add(movies.get(i).Series_Title);
      }
    }
    return list;
  }

    public List<String> getTopStars(int top_k, String by) {
        List<String> list = new ArrayList<>();
        if(by.equals("rating")){
         Map<String,double[]> map =new TreeMap<String,double[]>();

         for(int i=0;i<movies.size();i++){
             String star1= movies.get(i).Star1.trim();
             String star2= movies.get(i).Star2.trim();
             String star3= movies.get(i).Star3.trim();
             String star4= movies.get(i).Star4.trim();
             float rate = Float.parseFloat(movies.get(i).IMDB_Rating.trim());
             Iterator<String> iterator1 = map.keySet().iterator();
             boolean star1_not_in_map =true;
             while (iterator1.hasNext()){
                 String temp_list = iterator1.next();
                 if(temp_list.equals(star1)){
                     double num []=new double[3];
                     double number0 = map.get(temp_list)[0]+ rate;
                     double number1 =map.get(temp_list)[1]+1;
                     double number2 = (map.get(temp_list)[2]*(number1-1)+rate)/number1;

                     num[0]= number0;
                     num[1]= number1;
                     num[2]= number2;
                     map.put(temp_list,num);
                     star1_not_in_map =false;
                 }
             }
             if(star1_not_in_map){
                 double num []=new double[3];
                 num[0]=rate;
                 num[1]=1.0f;
                 num[2]=rate;
                 map.put(star1,num);
             }
             Iterator<String> iterator2 = map.keySet().iterator();
             boolean star2_not_in_map =true;
             while (iterator2.hasNext()){
                 String temp_list = iterator2.next();
                 if(temp_list.equals(star2)){
                     double num []=new double[3];
                     double number0 = map.get(temp_list)[0]+ rate;
                     double number1 =map.get(temp_list)[1]+1;
                     double number2 = (map.get(temp_list)[2]*(number1-1)+rate)/number1;

                     num[0]=number0;
                     num[1]= number1;
                     num[2]= number2;
                     map.put(temp_list,num);
                     star2_not_in_map =false;
                 }
             }
             if(star2_not_in_map){
                 double num []=new double[3];
                 num[0]=rate;
                 num[1]=1.0f;
                 num[2]=rate;
                 map.put(star2,num);
             }
             Iterator<String> iterator3 = map.keySet().iterator();
             boolean star3_not_in_map =true;
             while (iterator3.hasNext()){
                 String temp_list = iterator3.next();
                 if(temp_list.equals(star3)){
                     double num []=new double[3];
                     double number0 = map.get(temp_list)[0]+ rate;
                     double number1 =map.get(temp_list)[1]+1;
                     double number2 =(map.get(temp_list)[2]*(number1-1)+rate)/number1;
                     num[0]=number0;
                     num[1]=number1;
                     num[2]=number2;
                     map.put(temp_list,num);
                     star3_not_in_map =false;
                 }
             }
             if(star3_not_in_map){
                 double num []=new double[3];
                 num[0]=rate;
                 num[1]=1.0f;
                 num[2]=rate;
                 map.put(star3,num);
             }
             Iterator<String> iterator4 = map.keySet().iterator();
             boolean star4_not_in_map =true;
             while (iterator4.hasNext()){
                 String temp_list = iterator4.next();
                 if(temp_list.equals(star4)){
                     double num []=new double[3];
                     double number0 = map.get(temp_list)[0]+ rate;
                     double number1 =map.get(temp_list)[1]+1;
                     double number2 = (map.get(temp_list)[2]*(number1-1)+rate)/number1;

                     num[0]= number0;
                     num[1]=number1;
                     num[2]= number2;
                     map.put(temp_list,num);
                     star4_not_in_map =false;
                 }
             }
             if(star4_not_in_map){
                 double num []=new double[3];
                 num[0]=rate;
                 num[1]=1.0f;
                 num[2]=rate;
                 map.put(star4,num);
             }

             }



            Map<String,Double> map2=new TreeMap<String,Double>();
            for (Map.Entry<String, double[]> entry : map.entrySet()) {
                map2.put(entry.getKey(),entry.getValue()[2]);
            }
            Map<String, Double> sorted = map2.
                    entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(comparingByValue()))
                    .collect(
                            toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                    LinkedHashMap::new));

            int i=0;
            for (Map.Entry<String, Double> entry : sorted.entrySet()) {
                list.add(entry.getKey());
                i++;
                if(i==top_k){
                    break;
                }
            }
        }
        else {
            Map<String,Long[]> map =new TreeMap<String,Long[]>();
            for(int i=0;i<movies.size();i++){
                String star1= movies.get(i).Star1.trim();
                String star2= movies.get(i).Star2.trim();
                String star3= movies.get(i).Star3.trim();
                String star4= movies.get(i).Star4.trim();
                String temp_gross = movies.get(i).Gross.trim();
                temp_gross =temp_gross.replaceAll(",","");
                temp_gross =temp_gross.replaceAll(" ","");
                long gross =0;
                if(temp_gross.equals("")){
                 continue;
                }else {
                   gross = Long.parseLong(temp_gross);
                }
                Iterator<String> iterator1 = map.keySet().iterator();
                boolean star1_not_in_map =true;
                while (iterator1.hasNext()){
                    String temp_list = iterator1.next();
                    if(temp_list.equals(star1)){
                        Long num []=new Long[3];
                        Long number0 =map.get(temp_list)[0]+gross;
                        Long number1 =map.get(temp_list)[1]+1;
                        Long number2 =number0/number1;
                        num[0]=number0;
                        num[1]=number1;
                        num[2]=number2;
                        map.put(temp_list,num);
                        star1_not_in_map =false;
                    }
                }
                if(star1_not_in_map){
                    Long num []=new Long[3];
                    num[0] =gross;
                    num[1] = (long)1;
                    num[2] =gross;
                    map.put(star1,num);
                }
                Iterator<String> iterator2 = map.keySet().iterator();
                boolean star2_not_in_map =true;
                while (iterator2.hasNext()){
                    String temp_list = iterator2.next();
                    if(temp_list.equals(star2)){
                        Long num []=new Long[3];
                        Long number0 =map.get(temp_list)[0]+gross;
                        Long number1 =map.get(temp_list)[1]+1;
                        Long number2 =number0/number1;
                        num[0]=number0;
                        num[1]=number1;
                        num[2]=number2;
                        map.put(temp_list,num);
                        star2_not_in_map =false;
                    }
                }
                if(star2_not_in_map){
                    Long num []=new Long[3];
                    num[0] =gross;
                    num[1] = (long)1;
                    num[2] =gross;
                    map.put(star2,num);
                }
                Iterator<String> iterator3 = map.keySet().iterator();
                boolean star3_not_in_map =true;
                while (iterator3.hasNext()){
                    String temp_list = iterator3.next();
                    if(temp_list.equals(star3)){
                        Long num []=new Long[3];
                        Long number0 =map.get(temp_list)[0]+gross;
                        Long number1 =map.get(temp_list)[1]+1;
                        Long number2 =number0/number1;
                        num[0]=number0;
                        num[1]=number1;
                        num[2]=number2;
                        map.put(temp_list,num);
                        star3_not_in_map =false;
                    }
                }
                if(star3_not_in_map){
                    Long num []=new Long[3];
                    num[0] =gross;
                    num[1] = (long)1;
                    num[2] =gross;
                    map.put(star3,num);
                }
                Iterator<String> iterator4 = map.keySet().iterator();
                boolean star4_not_in_map =true;
                while (iterator4.hasNext()){
                    String temp_list = iterator4.next();
                    if(temp_list.equals(star4)){
                        Long num []=new Long[3];
                        Long number0 =map.get(temp_list)[0]+gross;
                        Long number1 =map.get(temp_list)[1]+1;
                        Long number2 =number0/number1;
                        num[0]=number0;
                        num[1]=number1;
                        num[2]=number2;
                        map.put(temp_list,num);
                        star4_not_in_map =false;
                    }
                }
                if(star4_not_in_map){
                    Long num []=new Long[3];
                    num[0] =gross;
                    num[1] = (long)1;
                    num[2] =gross;
                    map.put(star4,num);
                }




        }  Map<String,Long> map2=new TreeMap<String,Long>();
            for (Map.Entry<String, Long[]> entry : map.entrySet()) {
                map2.put(entry.getKey(),entry.getValue()[2]);
            }
            Map<String, Long> sorted = map2.
                    entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(comparingByValue()))
                    .collect(
                            toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                    LinkedHashMap::new));
            int j=0;
            for (Map.Entry<String, Long> entry : sorted.entrySet()) {
                list.add(entry.getKey());
                j++;
                if(j==top_k){
                    break;
                }
            }
        }
        return list;
    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime) {
        List<String> list = new ArrayList<>();
        for(int i=0;i<movies.size();i++){
            if(genreIsRight(movies.get(i), genre)&&is_min_rating(movies.get(i),min_rating)&&is_max_runtime(movies.get(i),max_runtime)){
                list.add(movies.get(i).Series_Title);
            }
        }
        for(int i=0;i<list.size();i++){
            for(int j=0;j<list.size();j++){
                if(list.get(i).compareTo(list.get(j))<=0){
                   Collections.swap(list,i,j);
                }
            }
        }

        return list;
    }
    public static boolean runTimeBigger(Movie movie1,Movie movie2){
        String time1 =movie1.Runtime.trim();
        String time2 =movie2.Runtime.trim();
        time1 =time1.substring(0,time1.length()-3).trim();
        time2 =time2.substring(0,time2.length()-3).trim();
        int t1 =Integer.parseInt(time1);
        int t2 =Integer.parseInt(time2);
        if(t1>t2){
            return true;
        }
        if(t1<t2){
            return false;
        }
        if(movie1.Series_Title.compareTo(movie2.Series_Title)<=0){
            return true;
        }else {
            return false;
        }

    }
    public static boolean overviewBigger(Movie movie1,Movie movie2){
        String  overview1 =movie1.Overview.trim();
        String  overview2 =movie2.Overview.trim();
        if(overview1.length()>overview2.length()){
            return true;
        }if(overview1.length()<overview2.length()){
            return false;
        }
        if(movie1.Series_Title.compareTo(movie2.Series_Title)<=0){
            return true;
        }else {
            return false;
        }
    }
    public static boolean genreIsRight(Movie movie,String genre){
        String genreTemp[] = movie.Genre.split(",");
        boolean answer =false;
        for (int j = 0; j < genreTemp.length; j++) {
            genreTemp[j] = genreTemp[j].trim();
            if(genreTemp[j].equals(genre)){
                answer=true;
            }
        }
        return answer;
    }

    public static boolean is_min_rating(Movie movie,float rate){
        if(Float.parseFloat(movie.IMDB_Rating)>=rate){
            return true;
        }else {
            return false;
        }
    }

    public static boolean is_max_runtime(Movie movie,int max_runtime){
        String runtime =movie.Runtime.trim();
        runtime =runtime.substring(0,runtime.length()-3).trim();
        int runtime1 =Integer.parseInt(runtime);
        if(runtime1<=max_runtime){
            return true;
        }else {
            return false;
        }
    }


}