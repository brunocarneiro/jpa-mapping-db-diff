package main;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import dto.Parameters;

/**
 * 
 * @author Bruno Carneiro
 *
 */
public class MappingDBChecker {
	
	public static Parameters params;
	
	public static void main (String [ ] args) throws Exception{
		params = new Parameters(args);
		List<File> files = new ArrayList<File>();
		String initialPath = params.getInitialPath();
		listFiles(files, initialPath);
		List<Class<?>> classes = getClassesBasedOnFiles(files, initialPath);
		Map<Class<?>, List<String>> mapeamentoPorClasse = getMapeamentoPorClasse(classes);
		Map<String, List<String>> colunasPorTabelas = getColunasPorTabela();
		Map<Class<?>, String> newTableNames = getTabelaNames(colunasPorTabelas, mapeamentoPorClasse);
		Map<String, List<String>> newColumnPerTableNames = getNewColumnNames(newTableNames, mapeamentoPorClasse, colunasPorTabelas);
		System.out.println(newColumnPerTableNames);
	}
	

	private static Map<String, List<String>> getNewColumnNames(Map<Class<?>, String> newTableNames, Map<Class<?>, List<String>> mapeamentoPorClasse, Map<String, List<String>> colunasPorTabelas ) {
		Map<String, List<String>> newColumnPerTableNames = new HashMap<String, List<String>>();

		int menorIndice = Integer.MAX_VALUE, indice;
		String newtableName, colunaEscolhida="";
		for(Class<?> clazz  : newTableNames.keySet()){
			newtableName = newTableNames.get(clazz);
			List<String> newColumns = colunasPorTabelas.get(newtableName);
			System.out.println(newTableNames.get(clazz));
				
			for(String newColumn : newColumns){
				for(String mapeamento : mapeamentoPorClasse.get(clazz)){
					indice=comparaStrings(mapeamento, newColumn);
					if(indice<menorIndice){
						menorIndice=indice;
						colunaEscolhida=mapeamento;
					}
					
				}
				String iniPrint="	";
				if(!newColumn.toLowerCase().equals(colunaEscolhida.toLowerCase())){
					iniPrint="X"+iniPrint;
				}
				System.out.println(iniPrint+newColumn + "->" + colunaEscolhida);
				menorIndice = Integer.MAX_VALUE;
			}
		}
			
		
		return newColumnPerTableNames;
	}


	public static Map<Class<?>, String> getTabelaNames(Map<String, List<String>> colunasPorTabelas, Map<Class<?>, List<String>> mapeamentoPorClasse){
		
		Map<Class<?>, String> tableEMapeamento=new HashMap<Class<?>, String>();
		int menorIndice=Integer.MAX_VALUE, indice;
		String tabelaEscolhida="";
		for(String tabela: colunasPorTabelas.keySet()){
			Class<?> classEscolhida=null;
			for(Class<?> clazz : mapeamentoPorClasse.keySet()){
				Table table = clazz.getAnnotation(Table.class);
				if(table!=null){
					String tableName = table.name();
					indice = comparaStrings(tableName, tabela);
					if(indice<menorIndice){
						menorIndice=indice;
						tabelaEscolhida=tableName;
						classEscolhida=clazz;
					}
						
				}
			}
			tableEMapeamento.put(classEscolhida, tabelaEscolhida);
			menorIndice=Integer.MAX_VALUE;
		}
		return tableEMapeamento;
	}
	
	public static int comparaStrings(String s1, String s2){
		int indice=0;
		for(int i=0; i<s1.length(); i++){
			if(s2.length()>i && s1.toLowerCase().charAt(i)!=s2.toLowerCase().charAt(i)){
				indice++;
			}
		}
		if(s1.length()!=s2.length()){
			indice+=Math.sqrt(Math.pow(s1.length()-s2.length(),2));
		}
		return indice;
	}
	
	public static Map<String, List<String>> getColunasPorTabela() throws Exception {
		Map<String, List<String>> colunasPorTabelas = new HashMap<String, List<String>>();
		Connection conn = getConnection();
		
		ResultSet rs = conn.createStatement().executeQuery("select * from user_tables");
		String tableName;
		ResultSet rs2;
		while(rs.next()){
			tableName = rs.getString("TABLE_NAME");
			List<String> columnNames=new ArrayList<String>();
			rs2 = conn.createStatement().executeQuery("select column_name from user_tab_columns where table_name = '"+tableName+"'");
			while(rs2.next()){
				columnNames.add(rs2.getString("column_name"));
			}
			colunasPorTabelas.put(tableName, columnNames);
		}
		return colunasPorTabelas;
	}
	
	public static Connection getConnection() throws Exception {
		Class.forName(params.getDriverClass());
	    Connection conn = null;
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", params.getDbUserName());
	    connectionProps.put("password", params.getDbPassword());

        conn = DriverManager.getConnection(params.getJdbcUrl(),
                   connectionProps);
	    return conn;
	}


	private static Map<Class<?>, List<String>> getMapeamentoPorClasse(
			List<Class<?>> classes) {
		Map<Class<?>, List<String>> mapeamentoPorClasse = new HashMap<Class<?>, List<String>>();
		List<Field> fields;
		for(Class<?> clazz : classes){ 
			if(clazz.getAnnotation(Entity.class)!=null){
				fields = getAllFields(clazz);
				List<String> columnNames = getColumnNames(fields);
				addInheritanceid(clazz, columnNames);
				mapeamentoPorClasse.put(clazz, columnNames);
			}
		}
		
		return mapeamentoPorClasse;
	}

	private static void addInheritanceid(Class<?> clazz,
			List<String> columnNames) {
		
		PrimaryKeyJoinColumn primaryKeyJoinColumn = clazz.getAnnotation(PrimaryKeyJoinColumn.class);
		if(primaryKeyJoinColumn!=null){
			columnNames.add(primaryKeyJoinColumn.name());
		}
	}


	private static List<Field> getAllFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		
		while(clazz.getSuperclass()!=null){
			clazz = clazz.getSuperclass();
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		}
		
		
		return fields;
	}

	private static List<String> getColumnNames(List<Field> fields) {
		List<String> columnNames = new ArrayList<String>();
		
		for(Field field : fields){
			if(field.getAnnotation(Column.class)!=null){
				columnNames.add(field.getAnnotation(Column.class).name());
			}
			else if(field.getAnnotation(JoinColumn.class)!=null){
				columnNames.add(field.getAnnotation(JoinColumn.class).name());
			}
			else if(field.getAnnotation(Embedded.class)!=null){
				columnNames.addAll(getColumnNames(getAllFields(field.getType())));
			}
		}
		return columnNames;
	}

	private static List<Class<?>> getClassesBasedOnFiles(List<File> files,
			String initialPath) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		
		for(File f: files){
			String className="";
			try {
				className=f.getAbsolutePath().replace(".java", "").replace(initialPath, "").replace("\\", ".");
				if(className.charAt(0)=='.')
					className=className.replaceFirst(".", "");
				classes.add(Class.forName(className));
			} catch (ClassNotFoundException e) {
				System.out.println("Class not found"+className);
			}
		}
		return classes;
	}

	public static void listFiles(List<File> files, String dir){
		
		File f = new File(dir);
		
		for (File file : f.listFiles()){
			if(file.isDirectory())
				listFiles(files, file.getPath());
			else
				files.add(file);
		}
	}
}
