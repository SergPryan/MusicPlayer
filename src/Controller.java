import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    private  ListView<String> listView;
    @FXML
    private Slider volumeSlider;
    @FXML
    private MediaView windowMediaView;
    @FXML
    private Button playButton;

    private static final String MEDIA_URL ="http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv";
    private MediaPlayer mediaPlayer;
    private String currentSong;
    private String fileChooserPath="\\";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> observableList= FXCollections.observableArrayList(MEDIA_URL);
        listView.getItems().addAll(observableList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setEditable(true);
        listView.setOnDragOver(new EventHandler<DragEvent>() {
             @Override
             public void handle(DragEvent event) {
                 Dragboard db = event.getDragboard();
                 if (db.hasFiles()) {
                     event.acceptTransferModes(TransferMode.LINK);}
                 event.consume();}});
        listView.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    for (File k : db.getFiles()){
                        try {
                            addPathAtListView(k.toPath());
                        } catch (IOException e) {
                            e.printStackTrace();}}}
                event.setDropCompleted(success);
                event.consume();}});
        volumeSlider.setValue(100);
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (mediaPlayer != null)
                { if (volumeSlider.isValueChanging()) {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);}}}});
    }


    public void playMediaContent(ActionEvent actionEvent) throws MalformedURLException {
        System.out.println(actionEvent);
        MediaPlayer.Status status=null;
        if (mediaPlayer != null) {status=mediaPlayer.getStatus();}
        if (status== MediaPlayer.Status.UNKNOWN
                || status== MediaPlayer.Status.HALTED
                ||listView.getItems().isEmpty()){
            return;}
        if (playButton.getText().equals("PAUSE")){
            mediaPlayer.pause();
            playButton.setText("PLAY");
            return;
        }
        String currentSelectionSong=listView.getSelectionModel().getSelectedItem().toString();
        if (mediaPlayer==null || !currentSong.equals(currentSelectionSong)) {
                currentSong=currentSelectionSong;
            try {
            mediaPlayer=new MediaPlayer(new Media(currentSong));}
            catch (IllegalArgumentException e){
                mediaPlayer=new MediaPlayer(new Media(Paths.get(currentSong).toUri().toString()));
            }
                windowMediaView.setMediaPlayer(mediaPlayer);
        }
        playButton.setText("PAUSE");
        mediaPlayer.play();
    }

    private void addPathAtListView(Path path) throws IOException{
        if (Files.isRegularFile(path)) {
            String fileName = path.getFileName().toString();
            for (MediaFileType x : MediaFileType.values()) {
                if (fileName.endsWith(x.toString())) {
                    listView.getItems().add(path.toString());
                    break;
                }
            }
        }
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
                for (Path file : directoryStream) {
                    addPathAtListView(file);}
            }
        }
    }

    public void openFileChooser(ActionEvent actionEvent) {
        FileChooser fileChooser=new FileChooser();
        fileChooser.setInitialDirectory(new File(fileChooserPath));
        fileChooser.setTitle("Выберите музыку");
        String[] extensionMediaArray=MediaFileType.getExtension();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(new String(Arrays.deepToString(extensionMediaArray)),extensionMediaArray));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files","*"));
        List<File> result=fileChooser.showOpenMultipleDialog(new Stage());
        try {
            if (result != null)
            {for (File x : result){
                addPathAtListView(x.toPath());
            }
            fileChooserPath=result.get(0).getParent();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeProgram(ActionEvent actionEvent) {
        System.exit(0);
    }
}


