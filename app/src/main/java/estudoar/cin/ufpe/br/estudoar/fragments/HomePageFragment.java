package estudoar.cin.ufpe.br.estudoar.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import estudoar.cin.ufpe.br.estudoar.R;
import estudoar.cin.ufpe.br.estudoar.activities.DoacoesActivity;
import estudoar.cin.ufpe.br.estudoar.activities.DoarActivity;


public class HomePageFragment extends Fragment implements View.OnClickListener {

    private Button doarBtn;
    private Button procurarBtn;
    private Button favoritosBtn;
    private Button interessadosBtn;

    public final int SEM_FILTRO = 0;
    public final int MEUS_FAVORITOS = 2;
    public final int MEUS_INTERESSADOS = 3;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.fragment_home_page, container, false);

        if(savedInstanceState == null){

        }

        doarBtn = (Button) homeView.findViewById(R.id.btnDoar);
        doarBtn.setOnClickListener(this);

        procurarBtn = (Button) homeView.findViewById(R.id.btnProcurar);
        procurarBtn.setOnClickListener(this);

        favoritosBtn = (Button) homeView.findViewById(R.id.btnFavoritos);
        favoritosBtn.setOnClickListener(this);

        interessadosBtn = (Button) homeView.findViewById(R.id.btnInteressados);
        interessadosBtn.setOnClickListener(this);

        return homeView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onClick(View v) {
        int key = v.getId();

        switch (key) {
            case R.id.btnDoar:
                goToDoarPage(v);
                break;
            case R.id.btnProcurar:
                goToProcurarPage(v);
                break;
            case R.id.btnFavoritos:
                goToFavoritosPage(v);
                break;
            case R.id.btnInteressados:
                goToInteressadosPage(v);
                break;
        }
    }

    public void goToDoarPage(View view) {
        Intent i = new Intent(getActivity(), DoarActivity.class);
        startActivity(i);
    }

    public void goToProcurarPage(View view) {
        Intent i = new Intent(getActivity(), DoacoesActivity.class);
        i.putExtra("filter",SEM_FILTRO);
        i.putExtra("title", "Doacoes");
        startActivity(i);
    }

    public void goToFavoritosPage(View view) {
        Intent i = new Intent(getActivity(), DoacoesActivity.class);
        i.putExtra("filter",MEUS_FAVORITOS);
        i.putExtra("title", "Meus Favoritos");
        startActivity(i);
    }

    public void goToInteressadosPage(View view){
        Intent i = new Intent(getActivity(), DoacoesActivity.class);
        i.putExtra("filter", MEUS_INTERESSADOS);
        i.putExtra("title", "Interessados");
        startActivity(i);
    }

}
