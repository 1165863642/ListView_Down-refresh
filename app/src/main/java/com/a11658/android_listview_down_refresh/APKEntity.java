package com.a11658.android_listview_down_refresh;

/**
 * @author Qiang
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class APKEntity {

    private int image;
    private String data;
    private String test;

    public APKEntity(int image, String data, String test){
        this.image = image;
        this.data = data;
        this.test = test;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getTest() {
        return test;
    }
}
