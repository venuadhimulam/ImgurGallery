package com.demo.imgurGallery.modal.ImgurModal;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ImgurData {

    private String id;
    private String title;
    private String desc;
    private String type;
    private String link;
    private String deletehash;

}
