package com.YaNan.frame.path;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Files 类
 * @version 1.0.1
 * @since jdk1.7
 * @enCoding UTF-8
 * @author YaNan
 *
 */
public class FileUtils {
	/**
	 * filePath String 型的文件地址
	 */
	public String filePath = null;
	/**
	 * file File 型的文件地址
	 */
	public File file = null;
	/**
	 * fileEncoding 读或写文件的编码 默认utf-8
	 */
	public String fileEncoding = "utf-8";
	/**
	 * fileContent 要写入的文件内容
	 */
	public String fileContent = null;
	/**
	 * notExistsCreate 当文件不存在时新建一个文件
	 */
	public boolean notExistsCreate = true;
	/**
	 * autoCR 自动回车，该版本已禁用
	 */
	public boolean autoCR = true;

	/**
	 * Files类 调用此类你将可以更简便的操作文本文件
	 * <p>
	 * 如果你实例化此类后没有使用参数，你需要先对该类进行赋值
	 * <p>
	 * 例如: 1 Files file = new Files();
	 * <p>
	 * file.File = new File("test.txt");
	 * <p>
	 * file.write("hello files");
	 * <p>
	 * 2 Files file = new File();
	 * <p>
	 * file.File = new Files("test.txt");
	 * <p>
	 * file.fileContent = "hello files";
	 * <p>
	 * file.write();
	 * 
	 * @see {@link #Files(String)}
	 * @see {@link #Files(File)}
	 */
	public FileUtils() {
	}

	/**
	 * Files类 需要传入一个String型的filePath 调用此类你将可以更简便的操作文本文件
	 * <p>
	 * 如果你实例化此类后没有使用参数，你需要先对该类进行赋值
	 * <p>
	 * 例如: 1 Files file = new Files("test.txt");
	 * <p>
	 * file.write("hello files");
	 * <p>
	 * 2 Files file = new Files("test.txt");
	 * <p>
	 * file.fileContent = "hello files";
	 * <p>
	 * file.write();
	 * 
	 * @see {@link #Files()}
	 * @see {@link #Files(File)}
	 */
	public FileUtils(String file) {
		this.filePath = file;
	}

	/**
	 * Files类 需要传入一个File型的filePath 调用此类你将可以更简便的操作文本文件
	 * <p>
	 * 如果你实例化此类后没有使用参数，你需要先对该类进行赋值
	 * <p>
	 * 例如: 1 Files file = new Files(new File("test.txt"));
	 * <p>
	 * file.write("hello files");
	 * <p>
	 * 2 Files file = new Files(new File("test.txt"));
	 * <p>
	 * file.fileContent = "hello files";
	 * <p>
	 * file.write();
	 * 
	 * @see {@link #Files()}
	 * @see {@link #Files(String)}
	 */
	public FileUtils(File file) {
		this.file = file;
	}

	/**
	 * prepend文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * <p>
	 * 调用此方法需要先设置file或filePath，fileContent否则抛出空指针
	 * <p>
	 * prepend()
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #prepend(String)}
	 * @see {@link #prepend(File, String)}
	 * @see {@link #prepend(String, String)}
	 */
	public boolean prepend() throws Exception {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return prepend(this.filePath, this.fileContent);
		if (this.file != null)
			return prepend(this.file, this.fileContent);
		return false;
	}

	/**
	 * prepend文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * <p>
	 * 调用此方法需要先设置file或filePath，否则抛出空指针
	 * <p>
	 * prepend(String fileContent)
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #prepend()}
	 * @see {@link #prepend(File, String)}
	 * @see {@link #prepend(String, String)}
	 */
	public boolean prepend(String fileContent) throws Exception {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return prepend(this.filePath, fileContent);
		if (this.file != null)
			return prepend(this.file, fileContent);
		return false;
	}

	/**
	 * prepend文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * 
	 * <p>
	 * prepend(String fileName||filePath,String fileContent)
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #prepend()}
	 * @see {@link #prepend(String)}
	 * @see {@link #prepend(File, String)}
	 */
	public boolean prepend(String fileName, String fileContent)
			throws Exception {
		return prepend(new File(fileName), fileContent);
	}

	/**
	 * prepend文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * <p>
	 * 调用此方法需要先设置file或filePath，否则抛出空指针
	 * <p>
	 * prepend(File fileName||filePath,String fileContent)
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #prepend()}
	 * @see {@link #prepend(String)}
	 * @see {@link #prepend(String, String)}
	 */
	public boolean prepend(File file, String fileContent) throws Exception {
		if (file == null)
			throw new NullPointerException();
		if (fileContent == null)
			throw new NullPointerException();
		if (!file.canRead())
			throw new fileException("Cann,t read the file");
		try {
			fileContent = fileContent
					+ (read(file).equals("") ? "" : read(file));
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos, fileEncoding);
			BufferedWriter bf = new BufferedWriter(osw);
			bf.write(fileContent);
			bf.close();
			osw.close();
			fos.close();
		} catch (IOException e) {
			throw new fileException(e);
		}
		return true;
	}

	/**
	 * prependLn文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * <p>
	 * 调用此方法需要先设置file或filePath，fileContent否则抛出空指针
	 * <p>
	 * prependLn()
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #prependLn(String)}
	 * @see {@link #prependLn(File, String)}
	 * @see {@link #prependLn(String, String)}
	 */
	public boolean prependLn() throws Exception {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return prependLn(this.filePath, this.fileContent);
		if (this.file != null)
			return prependLn(this.file, this.fileContent);
		return false;
	}

	/**
	 * prependLn文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * <p>
	 * 调用此方法需要先设置file或filePath，否则抛出空指针
	 * <p>
	 * prependLn(String fileContent)
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #prependLn()}
	 * @see {@link #prependLn(File, String)}
	 * @see {@link #prependLn(String, String)}
	 */
	public boolean prependLn(String fileContent) throws Exception {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return prependLn(this.filePath, fileContent);
		if (this.file != null)
			return prependLn(this.file, fileContent);
		return false;
	}

	/**
	 * prepend文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * 
	 * <p>
	 * prependLn(String fileName||filePath,String fileContent)
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #prependLn()}
	 * @see {@link #prependLn(String)}
	 * @see {@link #prependLn(File, String)}
	 */
	public boolean prependLn(String fileName, String fileContent)
			throws Exception {
		return prependLn(new File(fileName), fileContent);
	}

	/**
	 * prependLn文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * <p>
	 * 调用此方法需要先设置file或filePath，否则抛出空指针
	 * <p>
	 * prependLn(File fileName||filePath,String fileContent)
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #prependLn()}
	 * @see {@link #prependLn(String)}
	 * @see {@link #prependLn(String, String)}
	 */
	public boolean prependLn(File file, String fileContent) throws Exception {
		if (file == null)
			throw new NullPointerException();
		if (fileContent == null)
			throw new NullPointerException();
		if (!file.canRead())
			throw new fileException("Cann,t read the file");
		try {
			fileContent = fileContent
					+ (read(file).equals("") ? "" : "\n" + read(file));
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos, fileEncoding);
			BufferedWriter bf = new BufferedWriter(osw);
			bf.write(fileContent);
			bf.close();
			osw.close();
			fos.close();
		} catch (IOException e) {
			throw new fileException(e);
		}
		return true;
	}

	/**
	 * reWrite文件覆写，以文本形式覆写文件,默认编码为utf-8
	 * <p>
	 * 调用此方法需要先设置file或filePath，fileContent，否则抛出空指针
	 * <p>
	 * reWrite()
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #reWrite(String)}
	 * @see {@link #reWrite(File, String)}
	 * @see {@link #reWrite(String, String)}
	 */
	public boolean reWrite() throws Exception {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return reWrite(this.filePath, this.fileContent);
		if (this.file != null)
			return reWrite(this.file, this.fileContent);
		return false;
	}

	/**
	 * reWrite文件覆写，以文本形式覆写文件,默认编码为utf-8
	 * <p>
	 * 调用此方法需要先设置file或filePath，否则抛出空指针
	 * <p>
	 * reWrite(String content)
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #reWrite()}
	 * @see {@link #reWrite(File, String)}
	 * @see {@link #reWrite(String, String)}
	 */
	public boolean reWrite(String fileContent) throws Exception {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return reWrite(this.filePath, fileContent);
		if (this.file != null)
			return reWrite(this.file, fileContent);
		return false;
	}

	/**
	 * reWrite文件覆写，以文本形式覆写文件,默认编码为utf-8
	 * <p>
	 * reWrite(File fileName||filePath,String content)
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #reWrite()}
	 * @see {@link #reWrite(String)}
	 * @see {@link #reWrite(String, String)}
	 */
	public boolean reWrite(File file, String fileContent) throws Exception {
		if (!file.exists()) {
			if (!file.canRead())
				throw new fileException("Cann,t read the file");
			try {
				if (this.notExistsCreate) {
					file.createNewFile();
					write(file, fileContent);
				} else {
					throw new fileException("File is't exists");
				}
			} catch (IOException e) {
				throw new fileException(e.toString());
			}
		} else {
			try {
				FileOutputStream fos = new FileOutputStream(file);
				OutputStreamWriter osw = new OutputStreamWriter(fos,
						fileEncoding);
				BufferedWriter bf = new BufferedWriter(osw);
				bf.write(fileContent);
				bf.close();
				osw.close();
				fos.close();
			} catch (IOException e) {
				throw new fileException(e);
			}
		}
		return true;
	}

	/**
	 * reWrite文件覆写，以文本形式覆写文件,默认编码为utf-8
	 * <p>
	 * reWrite(String fileName||filePath,String content)
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #reWrite()}
	 * @see {@link #reWrite(String)}
	 * @see {@link #reWrite(File, String)}
	 */
	public boolean reWrite(String filePath, String fileContent)
			throws Exception {
		return reWrite(new File(filePath), fileContent);
	}

	/**
	 * write文件写入，以文本形式写入文件,默认编码为utf-8
	 * <p>
	 * 调用此方法需要先设置file或filePath，fileContent，否则抛出空指针
	 * <p>
	 * write()
	 * 
	 * @return boolean:true or false
	 * @return true or false
	 * @see {@link #write(String)}
	 * @see {@link #write(File, String)}
	 * @see {@link #write(String, String)}
	 */
	public boolean write() throws Exception {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return write(this.filePath, this.fileContent);
		if (this.file != null)
			return write(this.file, this.fileContent);
		return false;
	}

	/**
	 * write文件写入，以文本形式写入文件,默认编码为utf-8
	 * <p>
	 * 调用此方法需要先设置file或filePath，否则抛出空指针
	 * <p>
	 * write(String content)
	 * 
	 * @return boolean:true or false
	 * @return true or false
	 * @see {@link #write()}
	 * @see {@link #write(File, String)}
	 * @see {@link #write(String, String)}
	 */
	public boolean write(String fileContent) throws Exception {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return write(this.filePath, fileContent);
		if (this.file != null)
			return write(this.file, fileContent);
		return false;
	}

	/**
	 * write文件写入，以文本形式写入文件,默认编码为utf-8
	 * <p>
	 * write(File fileName||filePath,String content)
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #write()}
	 * @see {@link #write(String)}
	 * @see {@link #write(String, String)}
	 */
	public boolean write(File file, String fileContent) throws Exception {
		if (!file.exists()) {
			if (!file.canWrite())
				throw new fileException("Cann,t Write the file");
			try {
				if (this.notExistsCreate) {
					file.createNewFile();
					write(file, fileContent);
				} else {
					throw new fileException("File is't exists");
				}
			} catch (IOException e) {
				throw new fileException(e.toString());
			}
		} else {
			try {
				fileContent = (read(file).equals("") ? "" : read(file)
						+ (this.autoCR ? "\r\n" : ""))
						+ fileContent;
				FileOutputStream fos = new FileOutputStream(file);
				OutputStreamWriter osw = new OutputStreamWriter(fos,
						fileEncoding);
				BufferedWriter bf = new BufferedWriter(osw);
				bf.write(fileContent);
				bf.close();
				osw.close();
				fos.close();
			} catch (IOException e) {
				throw new fileException(e);
			}
		}
		return true;
	}

	/**
	 * write文件写入，以文本形式写入文件,默认编码为utf-8
	 * <p>
	 * write(String fileName||filePath,String content)
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #write()}
	 * @see {@link #write(String)}
	 * @see {@link #write(File, String)}
	 */
	public boolean write(String filePath, String fileContent) throws Exception {
		return write(new File(filePath), fileContent);
	}

	/**
	 * writeLn文件写入，以文本形式写入文件,默认编码为utf-8
	 * <p>
	 * 调用此方法需要先设置file或filePath，fileContent，否则抛出空指针
	 * <p>
	 * writeLn()
	 * 
	 * @return boolean:true or false
	 * @return true or false
	 * @see {@link #writeLn(String)}
	 * @see {@link #writeLn(File, String)}
	 * @see {@link #writeLn(String, String)}
	 */
	public boolean writeLn() throws Exception {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return writeLn(this.filePath, this.fileContent);
		if (this.file != null)
			return writeLn(this.file, this.fileContent);
		return false;
	}

	/**
	 * writeLn文件写入，以文本形式写入文件,默认编码为utf-8
	 * <p>
	 * 调用此方法需要先设置file或filePath，否则抛出空指针
	 * <p>
	 * writeLn(String content)
	 * 
	 * @return boolean:true or false
	 * @return true or false
	 * @see {@link #writeLn()}
	 * @see {@link #writeLn(File, String)}
	 * @see {@link #writeLn(String, String)}
	 */
	public boolean writeLn(String fileContent) throws Exception {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return writeLn(this.filePath, fileContent);
		if (this.file != null)
			return writeLn(this.file, fileContent);
		return false;
	}

	/**
	 * writeLn文件写入，以文本形式写入文件,默认编码为utf-8
	 * <p>
	 * writeLn(File fileName||filePath,String content)
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #writeLn()}
	 * @see {@link #writeLn(String)}
	 * @see {@link #writeLn(String, String)}
	 */
	public boolean writeLn(File file, String fileContent) throws Exception {
		if (!file.exists()) {
			if (!file.canWrite())
				throw new fileException("Cann,t Write the file");
			try {
				if (this.notExistsCreate) {
					file.createNewFile();
					writeLn(file, fileContent);
				} else {
					throw new fileException("File is't exists");
				}
			} catch (IOException e) {
				throw new fileException(e.toString());
			}
		} else {
			try {
				fileContent = (read(file).equals("") ? "" : read(file)
						+ (this.autoCR ? "\r\n" : ""))
						+ fileContent;
				FileOutputStream fos = new FileOutputStream(file);
				OutputStreamWriter osw = new OutputStreamWriter(fos,
						fileEncoding);
				BufferedWriter bf = new BufferedWriter(osw);
				bf.write(fileContent);
				bf.close();
				osw.close();
				fos.close();
			} catch (IOException e) {
				throw new fileException(e);
			}
		}
		return true;
	}

	/**
	 * writeLn文件写入，以文本形式写入文件,默认编码为utf-8
	 * <p>
	 * writeLn(String fileName||filePath,String content)
	 * 
	 * @return boolean:true or false
	 * @throws Exception
	 * @see {@link #writeLn()}
	 * @see {@link #writeLn(String)}
	 * @see {@link #writeLn(File, String)}
	 */
	public boolean writeLn(String filePath, String fileContent)
			throws Exception {
		return writeLn(new File(filePath), fileContent);
	}

	/**
	 * read文件读取，以文本形式读取文件,默认编码为utf-8
	 * <p>
	 * read(File fileName||filePath)
	 * 
	 * @return String text or error message
	 * @throws Exception
	 * @see {@link #read()}
	 * @see {@link #read(String)}
	 */
	public String read(File fileName) throws Exception {
		StringBuffer strBuffer = new StringBuffer();
		String strLine;
		if (!fileName.exists())
			throw new Exception("FileIsn'tExists");
		;
		if (!fileName.canRead())
			throw new fileException("Cann,t read the file");
		try {
			FileInputStream fis = new FileInputStream(fileName);
			InputStreamReader isr = new InputStreamReader(fis, fileEncoding);
			BufferedReader br = new BufferedReader(isr);
			while ((strLine = br.readLine()) != null) {
				if (strBuffer.length() > 0)
					strBuffer.append("\r\n");
				strBuffer.append(strLine);
			}
			br.close();
			isr.close();
			fis.close();
		} catch (IOException e) {
			throw new fileException(e);
		}
		return strBuffer.toString();
	}

	/**
	 * read文件读取，以文本形式读取文件,默认编码为utf-8
	 * <p>
	 * read(String fileName||filePath);
	 * 
	 * @return String text or error message
	 * @throws Exception
	 * @see {@link #read()}
	 * @see {@link #read(File)}
	 */
	public String read(String fileName) throws Exception {
		return read(new File(fileName));
	}

	/**
	 * read文件读取，以文本形式读取文件,默认编码为utf-8
	 * <p>
	 * read();需要先设置file或filePath，否者抛出空指针
	 * 
	 * @return String text or error message
	 * @throws Exception
	 * @see {@link #read(String)}
	 * @see {@link #read(File)}
	 */
	public String read() throws Exception {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return read(this.filePath);
		if (this.file != null)
			return read(this.file);
		return null;
	}

	/**
	 * Attribute获取文件属性，以文本形式读取文件,默认编码为utf-8
	 * <p>
	 * Attribute();需要先设置file或filePath，否者抛出空指针
	 * 
	 * @return String text or error message
	 * @throws Exception
	 * @see {@link #attribute(String)}
	 * @see {@link #attribute(File)}
	 */
	public String attribute() throws IOException, fileException {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return attribute(this.filePath);
		if (this.file != null)
			return attribute(this.file);
		throw new fileException("unKnow error");
	}

	/**
	 * Attribute获取文件属性，以文本形式读取文件,默认编码为utf-8
	 * <p>
	 * Attribute(String fileName||filePath);针
	 * 
	 * @return String text or error message
	 * @throws Exception
	 * @see {@link #attribute()}
	 * @see {@link #attribute(File)}
	 */
	public String attribute(String fileName) throws IOException {
		return attribute(new File(fileName));
	}

	public OutputStream getOutputStream() throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(this.file);
		return fos;
	}

	/**
	 * Attribute获取文件属性，以文本形式读取文件,默认编码为utf-8
	 * <p>
	 * Attribute(File fileName||filePath);需要先设置file或filePath，否者抛出空指针
	 * 
	 * @return String text or error message
	 * @throws Exception
	 * @see {@link #attribute(String)}
	 * @see {@link #attribute()}
	 */
	@SuppressWarnings("deprecation")
	public String attribute(File file) throws IOException {
		StringBuffer fileInfo = new StringBuffer();
		fileInfo.append("Exists : " + file.exists());
		fileInfo.append("\nAbsolute File : " + file.getAbsoluteFile());
		fileInfo.append("\nAbsolute Path : " + file.getAbsolutePath());
		fileInfo.append("\nCanonical Path : " + file.getCanonicalPath());
		fileInfo.append("\nName : " + file.getName());
		fileInfo.append("\nPath : " + file.getPath());
		fileInfo.append("\nParent File : " + file.getParentFile());
		fileInfo.append("\nIs File : " + file.isFile());
		fileInfo.append("\nIs Hidden : " + file.isHidden());
		fileInfo.append("\nCan Execute : " + file.canExecute());
		fileInfo.append("\nCan Read : " + file.canRead());
		fileInfo.append("\nCan Write : " + file.canWrite());
		fileInfo.append("\nFile to Path : " + file.toPath());
		fileInfo.append("\nFile to URI : " + file.toURI());
		fileInfo.append("\nFile to URL : " + file.toURL());
		fileInfo.append("\nFile to String : " + file.toString());
		return fileInfo.toString();
	}

	public static byte[] getBytes(File file) {
		long len = file.length();
		if(len>Integer.MAX_VALUE)
			len = Integer.MAX_VALUE;
		byte[] bytes;
		InputStream is = null;
		try {
			bytes = new byte[(int) file.length()];
			is = new FileInputStream(file);
			is.read(bytes, 0, (int) len);
		} catch (IOException e) {
			throw new RuntimeException("failed to read file \""+file+"\"",e);
		} finally {
			if(is!=null)
				try {
					is.close();
				} catch (IOException e) {
					throw new RuntimeException("failed to clase file inputstream at read file \""+file+"\"",e);
				}
		}
		return bytes;
	}
	public static byte[] getBytes(String fileName) {
		File file = new File(fileName);
		if(!file.exists())
			throw new RuntimeException("could not to read file \""+file+"\"",new FileNotFoundException());
		return getBytes(file);
	}
}
