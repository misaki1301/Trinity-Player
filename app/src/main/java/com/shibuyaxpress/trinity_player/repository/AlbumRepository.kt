package com.shibuyaxpress.trinity_player.repository

import com.shibuyaxpress.trinity_player.models.Album

class AlbumRepository {

    fun getAlbumFromRepository() : ArrayList<Album> {
        val albumList = ArrayList<Album>()
        albumList.add(Album(1,"Nothing but the beat","https://images-na.ssl-images-amazon.com/images/I/61CiylsB9KL.jpg","David Guetta",12))
        albumList.add(Album(2,"Crossroads", "http://www.generasia.com/w/images/thumb/1/19/fripSide_-_Crossroads_%28Limited_Editions%29.jpg/400px-fripSide_-_Crossroads_%28Limited_Editions%29.jpg","Fripside", 15))
        albumList.add(Album(3,"Iris","http://st.cdjapan.co.jp/pictures/l/14/19/VVCL-1350.jpg","Er Aoi",5))
        albumList.add(Album(4,"ADAMAS","https://i1.sndcdn.com/artworks-000473606532-vezg3z-t500x500.jpg","Lisa",4))
        albumList.add(Album(5,"Party Rock","https://i2.wp.com/freshstuff4you.com/wp-content/uploads/2018/03/Party-Rock-Anthem-Remix-Stems.jpg","LMFAO",8))
        return albumList
    }
}