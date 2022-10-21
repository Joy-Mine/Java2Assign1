import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MovieAnalyzer {
//    static String[][] data=new String[501][16];
    static ArrayList<String[]> data=new ArrayList<>();
    public static void main(String[] args) {
        MovieAnalyzer nb =new MovieAnalyzer("resources/imdb_top_500.csv");
//        //task1
//        System.out.println(Arrays.toString(nb.getMovieCountByYear().entrySet().toArray()));
//        //task2
//        System.out.println(Arrays.toString(nb.getMovieCountByGenre().entrySet().toArray()));
//        //task3
////        System.out.println(Arrays.toString(nb.getCoStarCount().entrySet().toArray()));
//        //task4
        System.out.println(nb.getTopMovies(20,"runtime"));
//        task5
//        System.out.println(nb.getTopStars(20,"gross"));
//        //task6
//        System.out.println(nb.searchMovies("Drama",(float)5.5,20000));
    }
    public MovieAnalyzer(String dataset_path){
        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(dataset_path), StandardCharsets.UTF_8)) {
//        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataset_path),"UTF-8"))) {
            String line;
            String[] row;
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                int pos=line.indexOf("jpg");
                line=line.substring(line.indexOf(".jpg\"")+6);
                row=line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
                if(row[0].charAt(0)=='"'){
                    row[0]=row[0].substring(1,row[0].length()-1);
                }
//                System.out.println(row[0]);
                data.add(row);
//                System.out.println(Arrays.toString(row));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public Map<Integer, Integer> getMovieCountByYear(){
        Map<Integer,Integer> ansMap = new TreeMap<>((o1, o2) -> o2-o1);
        Iterator<String[]> ite = data.iterator();
        while (ite.hasNext()){
            String[] row=ite.next();
            int year=Integer.parseInt(row[1]);
//            System.out.println(year);
            if(ansMap.containsKey(year))
                ansMap.replace(year,ansMap.get(year)+1);
            else ansMap.put(year,1);
        }
        return ansMap;
    }
    public Map<String, Integer> getMovieCountByGenre(){
        Map<String,Integer> ansMap = new HashMap<>();
        Iterator<String[]> ite = data.iterator();
        while (ite.hasNext()){
            String[] row=ite.next();
            ArrayList<String> genre=new ArrayList<>();
            int beign,pos;
            if(row[4].charAt(0)=='"'){
                beign=1;
                for(pos=beign;pos<row[4].length();++pos){
                    if (row[4].charAt(pos)==','){
                        genre.add(row[4].substring(beign,pos));
                        beign=pos+2;
                    }
                    else if(row[4].charAt(pos)=='"') {
                        genre.add(row[4].substring(beign,pos));
                        break;
                    }
                }
            }
            else
                genre.add(row[4]);
//            System.out.println(genre);
            for(String gen:genre){
                if(ansMap.containsKey(gen))
                    ansMap.replace(gen,ansMap.get(gen)+1);
                else ansMap.put(gen,1);
            }
        }

        Map<String,Integer> reMap = new LinkedHashMap<>();
        ansMap.entrySet()
                .stream()
                .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                .collect(Collectors.toList()).forEach(ele -> reMap.put(ele.getKey(), ele.getValue()));

        return reMap;
    }
    public Map<List<String>, Integer> getCoStarCount(){  //unsorted
        List<String> coStars;
        Map<List<String>,Integer> ansMap=new HashMap<>();
        Iterator<String[]> ite = data.iterator();
        while (ite.hasNext()) {
            String[] row = ite.next();
            for(int i=9;i<13;++i){
                for(int j=i+1;j<13;++j){
                    coStars=new ArrayList<>();
                    if(row[i].compareTo(row[j])<0){
                        coStars.add(row[i]);
                        coStars.add(row[j]);
                    }
                    else {
                        coStars.add(row[j]);
                        coStars.add(row[i]);
                    }
//                    System.out.println(coStars);
                    if(ansMap.containsKey(coStars))
                        ansMap.replace(coStars,ansMap.get(coStars)+1);
                    else ansMap.put(coStars,1);
                }
            }
        }
        return ansMap;
    }
    public List<String> getTopMovies(int top_k, String by){
        List<String> ansList=new ArrayList<>();
        if(by=="runtime"){
            Map<String,Integer> movieRuntime = new HashMap<>();
            Iterator<String[]> ite=data.iterator();
            while (ite.hasNext()){
                String[] row=ite.next();
                int time=0,pos=0;
                while (row[3].charAt(pos)!=' ') {
                    if(row[3].charAt(pos)=='"'){
                        ++pos;
                        continue;
                    }
                    time = time * 10 + row[3].charAt(pos) - '0';
                    ++pos;
                }
                movieRuntime.put(row[0],time);
            }
            Map<String,Integer> ansMap=new LinkedHashMap<>();
            movieRuntime.entrySet()
                    .stream()
                    .sorted(new Comparator<Map.Entry<String, Integer>>() {
                        @Override
                        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                            if(o1.getValue().compareTo(o2.getValue())!=0){
                                return o2.getValue().compareTo(o1.getValue());
                            }
                            else {
                                return o1.getKey().compareTo(o2.getKey());
                            }
                        }
                    })
//                    .collect(Collectors.toList())
                    .forEach(ele -> ansMap.put(ele.getKey(), ele.getValue()));
            Iterator<String> movieIte = ansMap.keySet().iterator();
            int k=0;
            while (movieIte.hasNext() && k<top_k){
                ansList.add(movieIte.next());
                ++k;
            }
        }
        else if(by=="overview"){
            Map<String,Integer> movieOverview = new HashMap<>();
            Iterator<String[]> ite=data.iterator();
            while (ite.hasNext()){
                String[] row=ite.next();
                movieOverview.put(row[0],row[6].length());
            }
            Map<String,Integer> ansMap=new LinkedHashMap<>();
//            movieOverview.entrySet()
//                    .stream()
//                    .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
//                    .collect(Collectors.toList()).forEach(ele -> ansMap.put(ele.getKey(), ele.getValue()));
            movieOverview.entrySet()
                    .stream()
                    .sorted(new Comparator<Map.Entry<String, Integer>>() {
                        @Override
                        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                            if(o1.getValue().compareTo(o2.getValue())!=0){
                                return o2.getValue().compareTo(o1.getValue());
                            }
                            else {
                                return o1.getKey().compareTo(o2.getKey()); //Maybe should be reverse lexicographical order
                            }
                        }
                    })
//                    .collect(Collectors.toList())
                    .forEach(ele -> ansMap.put(ele.getKey(), ele.getValue()));
            Iterator<String> movieIte = ansMap.keySet().iterator();
            int k=0;
            while (movieIte.hasNext() && k<top_k){
                ansList.add(movieIte.next());
                ++k;
            }
        }
        else {
            System.out.println("Wrong");
        }
        return ansList;
    }
    public List<String> getTopStars(int top_k, String by){
        List<String> ansList=new ArrayList<>();
        if(by=="rating"){
            class ratingInfo{
                double sum;
                int num;
                double avgRating;
                ratingInfo(double rating){
                    this.sum=rating;
                    this.num=1;
                }
                void update(double rating){
                    sum+=rating;
                    ++num;
                }
                void calculate(){
                    avgRating=sum/num;
                }
            }
            Map<String,ratingInfo> starAvgRating = new HashMap<>();
            Iterator<String[]> ite=data.iterator();
            while (ite.hasNext()){
                String[] row=ite.next();
                double rating=Double.parseDouble(row[5]);
                for(int i=9;i<13;++i){
                    String star=row[i];
                    if(starAvgRating.containsKey(star))
                        starAvgRating.get(star).update(rating);
                    else
                        starAvgRating.put(star,new ratingInfo(rating));
                }
            }
            Iterator<Map.Entry<String,ratingInfo>> iteStar=starAvgRating.entrySet().iterator();
            while (iteStar.hasNext())
                iteStar.next().getValue().calculate();
            Map<String,ratingInfo> ansMap=new LinkedHashMap<>();
            starAvgRating.entrySet()
                    .stream()
                    .sorted(new Comparator<Map.Entry<String, ratingInfo>>() {
                        @Override
                        public int compare(Map.Entry<String, ratingInfo> o1, Map.Entry<String, ratingInfo> o2) { //todo: the precision is too high(I'm using double)
                            return o1.getValue().avgRating>o2.getValue().avgRating ? 1 :
                                    (o1.getValue().avgRating==o2.getValue().avgRating)? 0 : -1;
                        }
                    })
//                    .collect(Collectors.toList())
                    .forEach(ele -> ansMap.put(ele.getKey(), ele.getValue()));
            Iterator<String> starIte = ansMap.keySet().iterator();
            int k=0;
            while (starIte.hasNext() && k<top_k){
                ansList.add(starIte.next());
                ++k;
            }
        }
        else if(by=="gross"){
            class grossInfo{
                long sum;
                int num;
                double avgGross;
                grossInfo(long gross){
                    this.sum=gross;
                    num=1;
                }
                void update(long gross){
                    sum+=gross;
                    ++num;
                }
                void calculate(){
                    this.avgGross=sum/num;
                }
            }
            Map<String,grossInfo> starAvgGross = new HashMap<>();
            Iterator<String[]> ite=data.iterator();
            while (ite.hasNext()){
                String[] row=ite.next();
//                long gross=Long.parseLong(row[15]);
                long gross=0;
                int pos=0;
                if(row.length<15)
                    continue;
                while (pos<row[14].length()){
                    if(row[14].charAt(pos)=='"' || row[14].charAt(pos)==',')
                        ++pos;
                    else {
                        gross=gross*10+row[14].charAt(pos++)-'0';
                    }
                }
                for(int i=9;i<13;++i){
                    String star=row[i];
                    if(starAvgGross.containsKey(star))
                        starAvgGross.get(star).update(gross);
                    else
                        starAvgGross.put(star,new grossInfo(gross));
                }
            }
            Iterator<Map.Entry<String,grossInfo>> iteStar=starAvgGross.entrySet().iterator();
            while (iteStar.hasNext())
                iteStar.next().getValue().calculate();
            Map<String,grossInfo> ansMap=new LinkedHashMap<>();
            starAvgGross.entrySet()
                    .stream()
                    .sorted(new Comparator<Map.Entry<String, grossInfo>>() {
                        @Override
                        public int compare(Map.Entry<String, grossInfo> o1, Map.Entry<String, grossInfo> o2) {//todo: the precision is too high
                            return o1.getValue().avgGross>o2.getValue().avgGross ? 1 :
                                    (o1.getValue().avgGross==o2.getValue().avgGross) ? 0 : -1;
                        }
                    })
//                    .collect(Collectors.toList())
                    .forEach(ele -> ansMap.put(ele.getKey(), ele.getValue()));
            Iterator<String> starIte = ansMap.keySet().iterator();
            int k=0;
            while (starIte.hasNext() && k<top_k){
                ansList.add(starIte.next());
                ++k;
            }
        }
        else {
            System.out.println("Wrong");
        }
        return ansList;
    }//todo: Maybe wong answer
    public List<String> searchMovies(String genre, float min_rating, int max_runtime){
        List<String> ansList = new ArrayList<>();
        data.stream()
                .filter(row -> {
                    int begin,pos=0;
                    float movieRating=Float.parseFloat(row[5]),movieRuntime=0;
                    while (row[3].charAt(pos)!=' '){
                        movieRuntime=movieRuntime*10+row[3].charAt(pos++)-'0';
                    }
                    if(row[4].charAt(0)=='"'){
                        begin=1;
                        for(pos=begin;pos<row[4].length();++pos){
                            if(row[4].charAt(pos)==',')
                            {
                                String aGenre=row[4].substring(begin,pos);
                                if(aGenre==genre){
//                                        movieList.add(new info(row[0],movieRating,movieRuntime));
                                    if(movieRating>=min_rating && movieRuntime<=max_runtime)
                                        return true;
                                }
                            }
                            else if(row[4].charAt(pos)=='"'){
                                String aGenre=row[4].substring(begin,pos);
                                if(aGenre==genre && movieRating>=min_rating && movieRuntime<=max_runtime)
                                    return true;
                            }
                        }
                    }
                    else
                        if(row[4]==genre &&movieRating>=min_rating && movieRuntime<=max_runtime)
                            return true;
                    return false;
                })
                .forEach(row -> ansList.add(row[0]));
        return ansList;
    }
}