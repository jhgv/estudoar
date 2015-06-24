package estudoar.cin.ufpe.br.estudoar;

import android.app.Fragment;
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
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DoacoesFragment extends Fragment implements AbsListView.OnItemClickListener{
    protected int filter;
    private ProgressBar spinner;
    protected List<ParseObject> mDoacoes;
    private OnFragmentInteractionListener mListener;
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

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);

        spinner = (ProgressBar) getActivity().findViewById(R.id.loadingProgress);

        if (filter == 1 || filter == 0) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");
            if (filter == 1) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                query.whereEqualTo("doador", currentUser);
            }
            doQuery(query);

        } else if (filter == 2) {
            ParseQuery<ParseObject> queryFavoritos = ParseQuery.getQuery("Favoritos");
            String usuarioAtual = ParseUser.getCurrentUser().getObjectId();
            queryFavoritos.whereEqualTo("interessado", usuarioAtual);

            ParseQuery<ParseObject> queryDoacoes = ParseQuery.getQuery("Doacao");
            queryDoacoes.whereMatchesKeyInQuery("objectId", "doacao", queryFavoritos);

            doQuery(queryDoacoes);

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
                if(newText.length() == 0){
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");
                    doQuery(query);
                }
                else if (newText.length() >= 4) {
                    mListView.setVisibility(View.INVISIBLE);
                    doQuerySearch(newText);
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

    public void doQuery(ParseQuery<ParseObject> query){
        spinner.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.INVISIBLE);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> doacoes, com.parse.ParseException e) {
                if (e == null) {
                    spinner.setVisibility(View.INVISIBLE);
                    mListView.setVisibility(View.VISIBLE);
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


}
