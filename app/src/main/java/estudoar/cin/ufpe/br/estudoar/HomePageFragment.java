package estudoar.cin.ufpe.br.estudoar;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class HomePageFragment extends Fragment implements View.OnClickListener {

    private Button doarBtn;
    private Button procurarBtn;
    private Button favoritosBtn;
    private Button interessadosBtn;

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
        i.putExtra("filter",0);
        i.putExtra("title", "Doacoes");
        startActivity(i);
    }

    public void goToFavoritosPage(View view) {
        Intent i = new Intent(getActivity(), DoacoesActivity.class);
        i.putExtra("filter",2);
        i.putExtra("title", "Meus Favoritos");
        startActivity(i);
    }

    public void goToInteressadosPage(View view){
        Intent i = new Intent(getActivity(), DoacoesActivity.class);
        i.putExtra("filter",3);
        i.putExtra("title", "Interessados");
        startActivity(i);
    }

}
