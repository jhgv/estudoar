package estudoar.cin.ufpe.br.estudoar;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DoacoesFragment extends Fragment implements AbsListView.OnItemClickListener {

    private int filter;


    private ProgressBar spinner;
    private List<ParseObject> mDoacoes;
    private OnFragmentInteractionListener mListener;

    private String currentUser = ParseUser.getCurrentUser().getObjectId();
    private String id_usuario = "";

    private ParseGeoPoint currentPoint;

    private boolean isGpsSearchActivated = false;

    private LinearLayout buscaAtivadaTxt;

    /**
     * ListView/GridView do fragment
     */
    private AbsListView mListView;
    Bundle extras;

    public DoacoesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (extras == null) {
            extras = intent.getExtras();
        }

        filter = extras.getInt("filter");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doacoes, container, false);

        buscaAtivadaTxt = (LinearLayout) view.findViewById(R.id.buscaAtivadaTxt);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);

        spinner = (ProgressBar) getActivity().findViewById(R.id.loadingProgress);

        Intent intent = getActivity().getIntent();
        filter = intent.getExtras().getInt("filter");

        if (filter == 4) {
            id_usuario = intent.getExtras().getString("id_usuario");
        }

//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            doQuerySearch(intent.getStringExtra(SearchManager.QUERY));
//        }

        doSimpleSearch();

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");

        switch (id) {
            case R.id.local_search:
                if(isGpsSearchActivated){
                    isGpsSearchActivated = false;
                    buscaAtivadaTxt.setVisibility(View.GONE);
                }else{
                    isGpsSearchActivated = true;
                }
                break;
        }
        doQuery(query);
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (filter == 2 || filter == 3 ) {
            menu.findItem(R.id.local_search).setVisible(false);
        }

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    doSimpleSearch();
                } else {
                    if (filter == 3) {
                        doQuerySearchFavoritos(newText);
                    } else {
                        doQuerySearchDoacoes(newText);
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                doQuerySearch(query);
                return true;
            }
        });

    }

    private void doSimpleSearch() {
        if (filter == 0 || filter == 1 || filter == 4) {
            final ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");

            if (filter == 1) {
                query.whereEqualTo("doador", currentUser);
            } else if (filter == 4) {
                query.whereEqualTo("doador", id_usuario);
            }
            doQuery(query);

        } else if (filter == 2) {
            ParseQuery<ParseObject> queryFavoritos = ParseQuery.getQuery("Favoritos");
            queryFavoritos.whereEqualTo("interessado", currentUser);

            ParseQuery<ParseObject> queryDoacoes = ParseQuery.getQuery("Doacao");
            queryDoacoes.whereMatchesKeyInQuery("objectId", "doacao", queryFavoritos);

            doQuery(queryDoacoes);

        } else if (filter == 3) {
            ParseQuery<ParseObject> queryDoacoes = ParseQuery.getQuery("Doacao");
            queryDoacoes.whereEqualTo("doador", currentUser);

            ParseQuery<ParseObject> queryInteressados = ParseQuery.getQuery("Favoritos");
            queryInteressados.whereMatchesKeyInQuery("doacao", "objectId", queryDoacoes);

            doQueryInteressados(queryInteressados);
        }

    }

    private void doQuerySearchDoacoes(String querySearch) {
        ParseQuery<ParseObject> queryNome = ParseQuery.getQuery("Doacao");
        queryNome.whereContains("nome", querySearch);

        ParseQuery<ParseObject> queryAssunto = ParseQuery.getQuery("Doacao");
        queryAssunto.whereContains("assunto", querySearch);

        ParseQuery<ParseObject> queryDesc = ParseQuery.getQuery("Doacao");
        queryDesc.whereContains("descricao", querySearch);

        ParseQuery<ParseObject> queryCateg = ParseQuery.getQuery("Doacao");
        queryCateg.whereContains("categoria", querySearch);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(queryNome);
        queries.add(queryAssunto);
        queries.add(queryDesc);
        queries.add(queryCateg);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

        if (filter == 1) {
            mainQuery.whereEqualTo("doador", currentUser);
        } else if (filter == 4) {
            mainQuery.whereEqualTo("doador", id_usuario);
        } else if (filter == 2) {
            ParseQuery<ParseObject> queryFavoritos = ParseQuery.getQuery("Favoritos");
            queryFavoritos.whereEqualTo("interessado", currentUser);
            mainQuery.whereMatchesKeyInQuery("objectId", "doacao", queryFavoritos);
        }

        doQuery(mainQuery);

    }

    private void doQuerySearch(String querySearch) {
        ParseQuery<ParseObject> queryNome = ParseQuery.getQuery("Doacao");
        queryNome.whereContains("nome", querySearch);

        ParseQuery<ParseObject> queryAssunto = ParseQuery.getQuery("Doacao");
        queryAssunto.whereContains("assunto", querySearch);

        ParseQuery<ParseObject> queryDesc = ParseQuery.getQuery("Doacao");
        queryDesc.whereContains("descricao", querySearch);

        ParseQuery<ParseObject> queryCateg = ParseQuery.getQuery("Doacao");
        queryCateg.whereContains("categoria", querySearch);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(queryNome);
        queries.add(queryAssunto);
        queries.add(queryDesc);
        queries.add(queryCateg);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

        doQuery(mainQuery);

    }

    private void doQuerySearchFavoritos(String querySearch) {
        ParseQuery<ParseUser> queryUserNickName = ParseUser.getQuery();
        queryUserNickName.whereContains("username", querySearch);

        ParseQuery<ParseUser> queryUserName = ParseUser.getQuery();
        queryUserName.whereContains("name", querySearch);

        List<ParseQuery<ParseUser>> queriesUser = new ArrayList<ParseQuery<ParseUser>>();
        queriesUser.add(queryUserNickName);
        queriesUser.add(queryUserName);

        ParseQuery<ParseUser> queryUsuarios = ParseQuery.or(queriesUser);

        ParseQuery<ParseObject> queryNome = ParseQuery.getQuery("Doacao");
        queryNome.whereContains("nome", querySearch);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(queryNome);

        ParseQuery<ParseObject> queryDoacoes = ParseQuery.or(queries);
        queryDoacoes.whereEqualTo("doador", currentUser);

        ParseQuery<ParseObject> queryDoacoesFavoritadas = ParseQuery.getQuery("Favoritos");
        queryDoacoesFavoritadas.whereMatchesKeyInQuery("doacao", "objectId", queryDoacoes);

        ParseQuery<ParseObject> queryUsuariosInteressados = ParseQuery.getQuery("Favoritos");
        queryUsuariosInteressados.whereMatchesKeyInQuery("interessado", "objectId", queryUsuarios);

        List<ParseQuery<ParseObject>> mainQueries = new ArrayList<ParseQuery<ParseObject>>();
        mainQueries.add(queryDoacoesFavoritadas);
        mainQueries.add(queryUsuariosInteressados);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(mainQueries);

        doQueryInteressados(mainQuery);


    }

    public void doQuery(ParseQuery<ParseObject> query) {
        spinner.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.INVISIBLE);

        if(isGpsSearchActivated){
            setCurrentPosition();
            query.whereWithinKilometers("localizacao", currentPoint, 15.0);

        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> doacoes, com.parse.ParseException e) {
                mListView.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                if (doacoes.size() == 0){
                    AlertDialog.Builder dig = new AlertDialog.Builder(getActivity());
                    switch (filter) {
                        case 0:
                            dig.setMessage("Nenhum usuario publicou doacoes!");
                            break;
                        case 1:
                            dig.setMessage("Voce ainda nao publicou doacoes!");
                            break;
                        case 2:
                            dig.setMessage("Voce ainda nao tem favoritos!");
                            break;
                        case 4:
                            dig.setMessage("Este usuario ainda nao publicou doacoes!");
                            break;
                    }

                    dig.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    });
                    dig.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    });

                    dig.show();

                } else if (e == null){

                    mDoacoes = doacoes;
                    DoacaoAdapter adapter = new DoacaoAdapter(mListView.getContext(), mDoacoes);
                    ((AdapterView<ListAdapter>) mListView).setAdapter(adapter);

                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                                long arg3) {
                            ParseObject doacao = (ParseObject) mDoacoes.get(position);

                            Intent i = new Intent(getActivity(), VerDoacaoActivity.class);

                            String doador = (String) doacao.get("doador");
                            i.putExtra("id_doacao", doacao.getObjectId());
                            i.putExtra("id_doador", doador);

                            startActivity(i);
                        }
                    });

                } else {
                    Log.d("doacao", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void doQueryInteressados(ParseQuery<ParseObject> queryInteressados) {
        mListView.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);

        if(isGpsSearchActivated){
            setCurrentPosition();
            queryInteressados.whereWithinKilometers("localizacao", currentPoint, 15.0);

        }

        queryInteressados.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> favts, com.parse.ParseException e) {
                mListView.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.INVISIBLE);

                if (favts.size() == 0) {
                    new AlertDialog.Builder(getActivity())
                        .setMessage("Ninguem se interessou ainda por suas doacoes!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        })
                        .show();

                    //menu_materiais.findItem(R.id.local_search).setVisible(false);

                } else {
                    if (e == null) {
                        final List<ParseObject> favoritos = favts;
                        InteressadosAdapter adapter = new InteressadosAdapter(mListView.getContext(), favoritos);
                        ((AdapterView<ListAdapter>) mListView).setAdapter(adapter);

                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                                    long arg3) {
                                ParseObject favorito = (ParseObject) favoritos.get(position);

                                Intent i = new Intent(getActivity(), MeuPerfil.class);
                                i.putExtra("id_usuario", (String) favorito.get("interessado"));
                                startActivity(i);
                            }
                        });
                    } else {
                        Log.d("doacao", "Error: " + e.getMessage());
                    }
                }
            }
        });
    }



    public void setCurrentPosition() {
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buscaAtivadaTxt.setVisibility(View.GONE);
            buildAlertMessageNoGps();
        } else {
            buscaAtivadaTxt.setVisibility(View.VISIBLE);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");

            if (filter == 1) {
                query.whereEqualTo("doador", currentUser);
            } else if (filter == 4) {
                query.whereEqualTo("doador", id_usuario);
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            double longitude = myLocation.getLongitude();
            double latitude = myLocation.getLatitude();
            currentPoint = new ParseGeoPoint(latitude, longitude);

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Seu GPS está desligado, você deseja ativar?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}

