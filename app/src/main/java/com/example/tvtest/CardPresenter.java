package com.example.tvtest;

import android.graphics.drawable.Drawable;

import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

/*
* Se utiliza un presentador para generar vistas y vincular objetos a ellas a pedido.
* Está estrechamente relacionado con el concepto de RecyclerView.Adapter,
* pero no se basa en la posición. El Framework Leanback implementa el concepto
* de adaptador mediante un ObjectAdapter, que hace referencia a una instancia de
* Presenter (o PresenterSelector).*/

/*
* Se utiliza un CardPresenter para generar Vistas y enlazar Objetos a ellas bajo demanda.
* Contiene una imagen CardView
*/
public class CardPresenter extends Presenter {
    private static final String TAG = "CardPresenter";

    private static final int CARD_WIDTH = 600;
    private static final int CARD_HEIGHT = 350;
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;
    private Drawable mDefaultCardImage;

    //Método para establecer los colores del Card
    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        view.setBackgroundColor(color);
        view.setInfoAreaBackgroundColor(color);
        //Se deben establecer ambos colores de fondo porque el fondo del view es visible temporalmente
        // durante las animaciones
    }


    //El método onCreateViewHolder() devuelve una vista de un elemento sin personalizar.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        Log.d(TAG, "onCreateViewHolder");

        /*
        Log.d() se utiliza para propósitos de depuración. Sirve cuando deseamos imprimir muchos
        mensajes para que pueda registrar el flujo exacto de la aplicación.
        Con el fin de mantener un registro de los valores de las variables.
        */

        sDefaultBackgroundColor =
                ContextCompat.getColor(parent.getContext(), R.color.default_background);
        sSelectedBackgroundColor =
                ContextCompat.getColor(parent.getContext(), R.color.selected_background);
        /*
         * This template uses a default image in res/drawable, but the general case for Android TV
         * will require your resources in xhdpi. For more information, see
         * https://developer.android.com/training/tv/start/layouts.html#density-resources
         */
        mDefaultCardImage = ContextCompat.getDrawable(parent.getContext(), R.drawable.movie);

        ImageCardView cardView =
                new ImageCardView(parent.getContext()) {
                    @Override
                    public void setSelected(boolean selected) {
                        updateCardBackgroundColor(this, selected);
                        super.setSelected(selected);
                    }
                };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);
        return new ViewHolder(cardView);
    }


    //onBindViewHolder() . Encargado de actualizar los datos de un ViewHolder ya existente.
    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Movie movie = (Movie) item;
        ImageCardView cardView = (ImageCardView) viewHolder.view;

        Log.d(TAG, "onBindViewHolder");
        if (movie.getCardImageUrl() != null) {
            cardView.setTitleText(movie.getTitle());
            cardView.setContentText(movie.getStudio());
            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
            Glide.with(viewHolder.view.getContext())
                    .load(movie.getCardImageUrl())
                    .centerCrop()
                    .error(mDefaultCardImage)
                    .into(cardView.getMainImageView());
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        Log.d(TAG, "onUnbindViewHolder");
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        // Remove references to images so that the garbage collector can free up memory
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}