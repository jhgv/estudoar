package estudoar.cin.ufpe.br.estudoar;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.widget.SearchView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class DoacoesFragment extends Fragment implements AbsListView.OnItemClickListener{

    protected Dialog progressDialog;

    protected List<ParseObject> mDoacoes;

    private OnFragmentInteractionListener mListener;

    private int filter = 0;
    private String id_usuario = "";

    /**
     * ListView/GridView do fragment
     */
    private AbsListView mListView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DoacoesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doacoes, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);

        Intent intent = getActivity().getIntent();
        filter = intent.getExtras().getInt("filter");

//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            doQuerySearch(intent.getStringExtra(SearchManager.QUERY));
//        }
        if (filter == 1 || filter == 0 || filter == 4) {
            final ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");

            if (filter == 1) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                query.whereEqualTo("doador", currentUser);
            }else if(filter == 4){
                id_usuario = intent.getExtras().getString("id_usuario");
                final UsuarioParse usuario = new UsuarioParse();

                ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
                queryUser.whereEqualTo("objectId", id_usuario);
                queryUser.getFirstInBackground(new GetCallback<ParseUser>() {
                    public void done(ParseUser doador, ParseException e) {
                        if (e == null) {
                            usuario.setUsuario(doador);
                        } else {
                            Toast.makeText(getActivity(), "Erro ao tentar acessar o doador", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                query.whereEqualTo("doador", usuario.getUsuario());
            }

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> doacoes, com.parse.ParseException e) {
                    if (e == null) {
                        mDoacoes = doacoes;
                        DoacaoAdapter adapter = new DoacaoAdapter(mListView.getContext(), mDoacoes);
                        ((AdapterView<ListAdapter>) mListView).setAdapter(adapter);

                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                                    long arg3) {
                                ParseObject doacao = (ParseObject) mDoacoes.get(position);

                                Intent i = new Intent(getActivity(), VerDoacaoActivity.class);

                                ParseUser doador = (ParseUser) doacao.get("doador");
                                i.putExtra("id_doacao", doacao.getObjectId());
                                i.putExtra("id_doador", doador.getObjectId());

                                startActivity(i);
                            }
                        });
                    } else {
                        Log.d("doacao", "Error: " + e.getMessage());
                    }
                }
            });

        } else if (filter == 2) {
            ParseQuery<ParseObject> queryFavoritos = ParseQuery.getQuery("Favoritos");
            String usuarioAtual = ParseUser.getCurrentUser().getObjectId();
            queryFavoritos.whereEqualTo("interessado", usuarioAtual);

            ParseQuery<ParseObject> queryDoacoes = ParseQuery.getQuery("Doacao");
            queryDoacoes.whereMatchesKeyInQuery("objectId", "doacao", queryFavoritos);

            queryDoacoes.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> doacoes, com.parse.ParseException e) {
                    if (e == null) {
                        mDoacoes = doacoes;
                        DoacaoAdapter adapter = new DoacaoAdapter(mListView.getContext(), mDoacoes);
                        ((AdapterView<ListAdapter>) mListView).setAdapter(adapter);

                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                                    long arg3) {
                                ParseObject doacao = (ParseObject) mDoacoes.get(position);

                                Intent i = new Intent(getActivity(), VerDoacaoActivity.class);

                                ParseUser doador = (ParseUser) doacao.get("doador");
                                i.putExtra("id_doacao", doacao.getObjectId());
                                i.putExtra("id_doador", doador.getObjectId());

                                startActivity(i);
                            }
                        });
                    } else {
                        Log.d("doacao", "Error: " + e.getMessage());
                    }
                }
            });

        }else if(filter == 3){
            ParseQuery<ParseObject> queryDoacoes = ParseQuery.getQuery("Doacao");
            queryDoacoes.whereEqualTo("doador", ParseUser.getCurrentUser());

            ParseQuery<ParseObject> queryInteressados = ParseQuery.getQuery("Favoritos");
            queryInteressados.whereMatchesKeyInQuery("doacao", "objectId", queryDoacoes);

            queryInteressados.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> favts, com.parse.ParseException e) {
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
            });
        }

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if( newText.length() == 4){
                    progressDialog = ProgressDialog.show(getActivity(), "", "Carregando...", true);
                    if(filter == 3){
                        doQuerySearchFavoritos(newText);
                    }else{
                        doQuerySearchDoacoes(newText);
                    }
                }
                else if( newText.length() > 4 ){
                    if(filter == 3){
                        doQuerySearchFavoritos(newText);
                    }else{
                        doQuerySearchDoacoes(newText);
                    }
                }else{
                    mListView.setVisibility(View.INVISIBLE);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("MyApp", "submeteu: " + query);
                return true;
            }
        });

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
            ParseUser currentUser = ParseUser.getCurrentUser();
            mainQuery.whereEqualTo("doador", currentUser);
        }else if (filter == 4){
            mainQuery.whereEqualTo("doador", id_usuario);
        }else if (filter == 2){
            ParseQuery<ParseObject> queryFavoritos = ParseQuery.getQuery("Favoritos");
            String usuarioAtual = ParseUser.getCurrentUser().getObjectId();
            queryFavoritos.whereEqualTo("interessado", usuarioAtual);
            mainQuery.whereMatchesKeyInQuery("objectId", "doacao", queryFavoritos);
        }

        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> doacoes, com.parse.ParseException e) {
                mListView.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                if (e == null) {
                    mDoacoes = doacoes;
                    DoacaoAdapter adapter = new DoacaoAdapter(mListView.getContext(), mDoacoes);
                    ((AdapterView<ListAdapter>) mListView).setAdapter(adapter);

                    getActivity().getIntent().removeExtra("filter");

                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                                long arg3) {
                            ParseObject doacao = (ParseObject) mDoacoes.get(position);

                            Intent i = new Intent(getActivity(), VerDoacaoActivity.class);

                            ParseUser doador = (ParseUser) doacao.get("doador");
                            i.putExtra("id_doacao", doacao.getObjectId());
                            i.putExtra("id_doador", doador.getObjectId());

                            startActivity(i);
                        }
                    });
                } else {
                    Log.d("doacao", "Error: " + e.getMessage());
                }
            }
        });

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
        queryDoacoes.whereEqualTo("doador", ParseUser.getCurrentUser());

        ParseQuery<ParseObject> queryDoacoesFavoritadas = ParseQuery.getQuery("Favoritos");
        queryDoacoesFavoritadas.whereMatchesKeyInQuery("doacao", "objectId", queryDoacoes);

        ParseQuery<ParseObject> queryUsuariosInteressados = ParseQuery.getQuery("Favoritos");
        queryUsuariosInteressados.whereMatchesKeyInQuery("interessado", "objectId", queryUsuarios);

        List<ParseQuery<ParseObject>> mainQueries = new ArrayList<ParseQuery<ParseObject>>();
        mainQueries.add(queryDoacoesFavoritadas);
        mainQueries.add(queryUsuariosInteressados);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(mainQueries);

        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> favts, com.parse.ParseException e) {
                mListView.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                if (e == null) {
                    final List<ParseObject> favoritos = favts;
                    InteressadosAdapter adapter = new InteressadosAdapter(mListView.getContext(), favoritos);
                    ((AdapterView<ListAdapter>) mListView).setAdapter(adapter);

                    getActivity().getIntent().removeExtra("filter");

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
        });

    }
}

class UsuarioParse{
    public ParseUser usuario;

    UsuarioParse(){}

    public void setUsuario(ParseUser usuario){
        this.usuario = usuario;
    }

    public ParseUser getUsuario(){
        return this.usuario;
    }
}
