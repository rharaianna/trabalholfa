/**
 * Classe para a criacao de um automato finito deterministico
 * @author João Pedro Salim, Nina Aguiar, Rhara Ianna
*/

package afds;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AFD {
	private ConjuntoSimbolo simbolos;
	private ConjuntoSimbolo delimitador;
	private ConjuntoEstados estados;
	private ConjuntoEstados estadosFinais;
	private ConjuntoTransicaoD funcaoPrograma;
	private Estado estadoInicial;

	/**
	 * Metodo construtor de um Automato finito deterministico
	 * 
	 * @param simbolos
	 *                       ConjuntoSimbolo que representa o alfabeto do automato
	 *                       finito deterministico
	 * @param estados
	 *                       ConjuntoEstados que representa o conjunto de estados do
	 *                       automato finito deterministico
	 * @param funcaoPrograma
	 *                       ConjuntoTransicaoD que representa a funcao programa do
	 *                       automato finito deterministico
	 * @param estadoInicial
	 *                       Estado que representa o estado inicial do automato
	 *                       finito deterministico
	 * @param estadosFinais
	 *                       ConjuntoEstados que representa o conjunto de estados
	 *                       finais do automato finito deterministico
	 */
	public AFD(ConjuntoSimbolo simbolos, ConjuntoSimbolo delimitador, ConjuntoEstados estados,
			ConjuntoTransicaoD funcaoPrograma, Estado estadoInicial,
			ConjuntoEstados estadosFinais) {
		this.simbolos = simbolos.clonar();
		this.delimitador = delimitador.clonar();
		this.estados = estados.clonar();
		this.funcaoPrograma = funcaoPrograma.clonar();
		this.estadoInicial = estadoInicial.clonar();
		this.estadosFinais = estadosFinais.clonar();
	}

	public AFD() {
		simbolos = new ConjuntoSimbolo();
		estados = new ConjuntoEstados();
		estadosFinais = new ConjuntoEstados();
		funcaoPrograma = new ConjuntoTransicaoD();
		delimitador = new ConjuntoSimbolo();
	}

	public Estado getEstadoInicial() { return this.estadoInicial.clonar(); }
	public void setEstadoInicial(Estado estadoInicial) {
		this.estadoInicial = estadoInicial.clonar();
	}

	public ConjuntoEstados getEstados() {
		return this.estados.clonar();
	}
	public void setEstados(ConjuntoEstados estados) {
		this.estados = estados.clonar();
	}

	public ConjuntoSimbolo getDelimitador() { return delimitador;}
	public void setDelimitador(ConjuntoSimbolo delimitador) { this.delimitador = delimitador;}

	public ConjuntoEstados getEstadosFinais() {
		return this.estadosFinais.clonar();
	}
	public void setEstadosFinais(ConjuntoEstados estadosFinais) {
		this.estadosFinais = estadosFinais.clonar();
	}


	public ConjuntoTransicaoD getFuncaoPrograma() {
		return this.funcaoPrograma.clonar();
	}
	public void setFuncaoPrograma(ConjuntoTransicaoD funcaoPrograma) {	this.funcaoPrograma = funcaoPrograma.clonar();}


	public ConjuntoSimbolo getSimbolos() {
		return this.simbolos.clonar();
	}
	public void setSimbolos(ConjuntoSimbolo simbolos) {
		this.simbolos = simbolos.clonar();
	}


	public AFD clonar() {
		return new AFD(simbolos, delimitador, estados, funcaoPrograma, estadoInicial, estadosFinais);
	}

	public String toString() {
		String s = new String();
		s += "(";
		s += simbolos.toString();
		s += ",";
		s += estados.toString();
		s += ",";
		s += this.getFuncaoPrograma().toString();
		s += ",";
		s += estadoInicial.toString();
		s += ",";
		s += estadosFinais.toString();
		s += ")";
		return s;
	}

	/**
	 * Le as informaoces de um AFN em um arquivo XML passado como parametro
	 * 
	 * @param pathArquivo
	 *                    define o arquivo de onde será lido as informacoes do
	 *                    automato
	 * @return retorna o automato lido
	 */
	public void ler(String pathArquivo) throws Exception {
		String xmlPathname = pathArquivo;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(xmlPathname);

		Element elem = doc.getDocumentElement();
		NodeList nl0 = elem.getElementsByTagName("simbolos");
		NodeList nl1 = elem.getElementsByTagName("estados");
		NodeList nl2 = elem.getElementsByTagName("estadosFinais");
		NodeList nl3 = elem.getElementsByTagName("funcaoPrograma");
		NodeList nl4 = elem.getElementsByTagName("estadoInicial");
		NodeList nl5 = elem.getElementsByTagName("delimitador");

		// Leitura Símbolos
		lerBlocoSimbolos((Element) nl0.item(0));

		getChildTagValue(0, (Element) nl0.item(0), "elemento");
		getChildTagValue(1, (Element) nl1.item(0), "elemento");
		getChildTagValue(2, (Element) nl2.item(0), "elemento");
		getChildTagValue((Element) nl3.item(0), "elemento");


		//Le valor do delimitador
		Element delimit = (Element) nl5.item(0);
		String valor = delimit.getAttribute("valor");
		delimitador.inclui(new Simbolo(valor.charAt(0)));


		//Le valor do estado inicial
		Element eI = (Element) nl4.item(0);
		estadoInicial = new Estado(eI.getAttribute("valor"));

	}

	private void lerBlocoSimbolos(Element simbolosElem) throws Exception {
		NodeList bloco = simbolosElem.getElementsByTagName("bloco");

		for (int i = 0; i < bloco.getLength(); i++) {
			Element blocoElem = (Element) bloco.item(i);
			List<Character> lista = expandirSimbolos(blocoElem.getAttribute("valor"));

			for (char c : lista) {
				simbolos.inclui(new Simbolo(c));
			}
		}
	}

	//Lê conjuntos
	private void getChildTagValue(int tipo, Element elem, String tagName)
			throws Exception {
		NodeList children = elem.getElementsByTagName(tagName);

		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);

				if (child != null) {
					switch (tipo) {
						case 0:
							char[] c = child.getAttribute("valor").toCharArray();
							simbolos.inclui(new Simbolo(c[0]));
							break;
						case 1:
							estados.inclui(new Estado(child.getAttribute("valor")));
							break;
						case 2:
							estadosFinais.inclui(new Estado(child
									.getAttribute("valor")));
							break;
					}
				}
			}
		}
	}

	//Lê Transições
	private void getChildTagValue(Element elem, String tagName) throws Exception {
		TransicaoD transD = new TransicaoD();
		NodeList children = elem.getElementsByTagName(tagName);
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				if (child != null) {
					String origem = child.getAttribute("origem");

					NodeList destinos = child.getElementsByTagName("destino");
					for (int j = 0; j < destinos.getLength(); j++) {
						Element destinoElem = (Element) destinos.item(j);
						String destino = destinoElem.getAttribute("destino");
						String simbolos = destinoElem.getAttribute("simbolo");

						adicionaTransicoes(origem, destino, simbolos);
					}
				}
			}
		}
	}

	private List<Character> expandirSimbolos(String simbolosStr) {
		List<Character> lista = new ArrayList<>();

		String[] partes = simbolosStr.split(",");

		//Pra se a transição for um espaço ela não ser apagada pelo trim
		for (String p : partes) {
			if (p.equals(" ")) {
				lista.add(' ');
				continue;
			}

			p = p.trim();
			if (p.isEmpty()) continue;

			//Só tem como fazer essa sequência se o início e o fim sao compostos de um unico char
			if (p.length() == 3 && p.charAt(1) == '-') {
				char inicio = p.charAt(0);
				char fim = p.charAt(2);

				for (char c = inicio; c <= fim; c++) {
					lista.add(c);
				}
			} else {
				lista.add(p.charAt(0));
			}
		}

		return lista;
	}



	private void adicionaTransicoes(String origem, String destino, String simbolosStr)
			throws Exception {

		List<Character> lista = expandirSimbolos(simbolosStr);

		for (char c : lista) {

			Simbolo s = new Simbolo(c);

			if (!this.simbolos.pertence(s)) {
				throw new Exception("Erro no XML: O símbolo '" + c + "' usado na transição de " + origem + " para " + destino + " não foi declarado no alfabeto <simbolos>.");
			}

			TransicaoD nova = new TransicaoD();
			nova.setOrigem(new Estado(origem));
			nova.setDestino(new Estado(destino));
			nova.setSimbolo(new Simbolo(c));
			funcaoPrograma.inclui(nova);
		}
	}



// Fim das mudanças ----------------------------------------------------------------------------------------------------------------

	// Limpa a estrutura de dados do AFD
	private void limpa() {
		// limpa Alfabeto
		simbolos.limpar();
		//limpa Delimitador
		delimitador.limpar();
		// limpa conjunto de estados
		estados.limpar();
		// limpa Funcao Programa
		funcaoPrograma.limpar();
		// Limpa estados finais
		estadosFinais.limpar();
	}


	public Estado p(Estado e, Simbolo s) {
		ConjuntoTransicaoD fp;
		TransicaoD t;
		fp = getFuncaoPrograma();
		for (Iterator iter = fp.getElementos().iterator(); iter.hasNext();) {
			t = (TransicaoD) iter.next();
			if (t.getOrigem().igual(e) && t.getSimbolo().igual(s))
				return t.getDestino();
		}
		return null;
	}

	public Estado pe(Estado e, String p) {
		Estado eAtual = e;
		Simbolo s;
		int i = 0;
		while (i < p.length()) {
			s = new Simbolo(p.charAt(i));
			eAtual = p(eAtual, s);
			if (eAtual == null)
				return null;
			i++;
		}
		return eAtual;
	}


	public boolean Aceita(String p) {
		return getEstadosFinais().pertence(pe(getEstadoInicial(), p));
	}


	public void toXML(String filename) throws IOException {
		FileWriter writer = new FileWriter(filename + ".xml");
		PrintWriter saida = new PrintWriter(writer);

		saida.println("<AFD>");
		saida.println();

		saida.println("\t<simbolos>");
		for (Object s : this.getSimbolos().getElementos()) {
			saida.println("\t\t<elemento valor= \"" + s.toString() + "\"/>");
		}
		saida.println("\t</simbolos>");
		saida.println();

		saida.println("\t<delimitador valor= \"" + this.getDelimitador().toString() + "\"/>");
		saida.println();

		saida.println("\t<estados>");
		for (Object s : this.getEstados().getElementos()) {
			saida.println("\t\t<elemento valor= \"" + s.toString() + "\"/>");
		}
		saida.println("\t</estados>");
		saida.println();

		saida.println("\t<estadosFinais>");
		for (Object s : this.getEstadosFinais().getElementos()) {
			saida.println("\t\t<elemento valor= \"" + s.toString() + "\"/>");
		}
		saida.println("\t</estadosFinais>");
		saida.println();

		saida.println("\t<funcaoPrograma>");

		//Itera sobre todos os estados:
		for(Iterator iterS = estados.iterator(); iterS.hasNext();){
			Estado e = (Estado) iterS.next();
			List<TransicaoD> transicoes = funcaoPrograma.getTransicoesSaindoDe(e);
			if(transicoes.isEmpty()){
				continue;
			}
			saida.println("\t\t<elemento origem= \"" + e.getNome() + "\">");
			for(Iterator<TransicaoD> iterT = transicoes.iterator(); iterT.hasNext();){
				TransicaoD t = iterT.next();
				saida.println("\t\t\t<destino destino=\"" + t.getDestino() + "\" simbolo=\"" + t.getSimbolo() + "\"/>");
			}
			
			saida.println("\t\t</elemento>");
		}

		saida.println("\t</funcaoPrograma>");
		saida.println();

		saida.println("\t<estadoInicial valor= \"" + this.getEstadoInicial().toString() + "\"/>");
		saida.println();

		saida.println("</AFD>");

		saida.close();
		writer.close();
	}
}
