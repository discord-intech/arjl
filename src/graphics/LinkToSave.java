package graphics;

import java.io.Serializable;


/**
 * Class qui à partir d'une ElementOfLink créer un element simple qui peut être serialisé.
 */
public class LinkToSave implements Serializable {
    //Les indexs des 2 images lié au lien
    int indexImage1;
    int indexImage2;

    /**
     * Constructeur
     * @param element ElementOfLink
     */
    public LinkToSave(ElementOfLink element) {

        indexImage1=DeviceManager.getArray_list_of_image().indexOf(element.img1);
        indexImage2=DeviceManager.getArray_list_of_image().indexOf(element.img2);
    }

    @Override
    public String toString() {
        String str = "IndexImage1 : "+ indexImage1 + "\nIndexImage2 : " + indexImage2;

        return str;
    }
}
