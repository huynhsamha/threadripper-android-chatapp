package com.chatapp.threadripper.api;

import com.chatapp.threadripper.authenticated.models.Contact;

import java.util.ArrayList;

public class ApiService {

    OnCompleteListener listener;

    public static ApiService getInstance() { return new ApiService(); }

    public ApiService() { }


    public void getContactsList(OnCompleteListener listener) {
        ArrayList<Contact> data = new ArrayList<>();
        String name[] = {"Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris", "Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris"};
        String img[] = {
                "http://2sao.vietnamnetjsc.vn/2016/07/01/23/15/xtm1a.jpg",
                "https://znews-photo-td.zadn.vn/w660/Uploaded/bpivpjbp/2018_08_20/mpen180803MB002__1.jpg",
                "http://nguoi-noi-tieng.com/photo/tieu-su-dien-vien-xa-thi-man-6850.jpg",
                "https://2sao.vietnamnetjsc.vn/images/2018/07/17/11/43/xa-thi-man-4.jpg",
                "https://cdn.iconscout.com/icon/free/png-256/avatar-369-456321.png",
                "https://cdn1.iconfinder.com/data/icons/business-charts/512/customer-512.png",
                "http://abc.com/abc.jpg",
                "http://abc.com/abc.jpg",
                "http://abc.com/abc.jpg",
                "http://abc.com/abc.jpg"
        };

        for (int i = 0; i < 10; i++) {
            Contact contact = new Contact();
            contact.setName(name[i]);
            contact.setImage(img[i]);
            data.add(contact);
        }

        if (listener != null)
            listener.onSuccess(data);
    }

    public interface OnCompleteListener {
        public void onSuccess(ArrayList<Contact> contactsList);
        public void onFailure(String message);
    }
}
