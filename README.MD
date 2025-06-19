## Configuração do Ambiente com Máquinas Virtuais Debian

Para configurar o ambiente, utilizaremos **quatro máquinas virtuais** baseadas em imagens **Debian Linux**. A criação de cada máquina se baseia em práticas específicas do "Manual do Debian Linux", conforme detalhado abaixo:

* **OpenVPN Server**: Configuraremos esta máquina seguindo as instruções do **Capítulo 28** do "Manual do Debian Linux".
* **Firewall**: Esta máquina será configurada com base nas práticas do **Capítulo 16** do "Manual do Debian Linux".
* **Tomcat**: Para o servidor Tomcat, utilizaremos as diretrizes do **Capítulo 17** do "Manual do Debian Linux". Além disso, será necessário instalar o Python e o pip com os seguintes comandos:

    ```bash
    sudo apt install python3
    sudo apt install python3-pip
    pip install pymysql
    ```

---
## Configuração do Ambiente com Máquinas Virtuais Debian

Para configurar o ambiente, utilizaremos **cinco máquinas virtuais** baseadas em imagens **Debian Linux**. A criação de cada máquina se baseia em práticas específicas do "Manual do Debian Linux", conforme detalhado abaixo:

* **OpenVPN Server**: Configuraremos esta máquina seguindo as instruções do **Capítulo 28** do "Manual do Debian Linux".
* **Firewall**: Esta máquina será configurada com base nas práticas do **Capítulo 16** do "Manual do Debian Linux".
* **Tomcat**: Para o servidor Tomcat, utilizaremos as diretrizes do **Capítulo 17** do "Manual do Debian Linux". Além disso, será necessário instalar o Python e o pip com os seguintes comandos:

    ```bash
    sudo apt install python3
    sudo apt install python3-pip
    pip install pymysql
    ```
* **MariaDB**: Esta máquina virtual utilizará uma imagem Debian como base e terá o MariaDB instalado com os seguintes comandos:

    ```bash
    sudo apt update
    sudo apt install mariadb-client -y
    sudo apt install mariadb-server -y
    ```

Para auxiliar na administração dessas máquinas, é **altamente recomendado** que você siga a prática do **Capítulo 15** do "Manual do Debian Linux". Ele aborda a conexão via **SSH** e a transferência de arquivos usando o **WinSCP**, habilidades essenciais para gerenciar suas VMs.

---
### Configuração e Verificação do MariaDB

Para permitir conexões externas ao seu servidor MariaDB, você precisará editar o arquivo de configuração e reiniciar o serviço. Siga os passos abaixo:

1.  **Edite o arquivo de configuração:**
    Abra o arquivo de configuração do MariaDB utilizando o editor `nano`:

    ```bash
    sudo nano /etc/mysql/mariadb.conf.d/50-server.cnf
    ```

    Dentro do arquivo, localize a linha que começa com `bind-address` e altere-a de `127.0.0.1` para `0.0.0.0`. Isso permitirá que o MariaDB aceite conexões de qualquer endereço IP.

    ```
    bind-address = 0.0.0.0  # Altere para permitir conexões externas
    ```

2.  **Reinicie o serviço MariaDB:**
    Após salvar as alterações no arquivo de configuração, reinicie o serviço para que as mudanças tenham efeito:

    ```bash
    sudo systemctl restart mariadb
    ```

3.  **Verifique o status do serviço:**
    Para confirmar se o MariaDB está escutando na porta 3306 e aceitando conexões de qualquer endereço (0.0.0.0), execute o seguinte comando:

    ```bash
    sudo ss -tulnp | grep 3306
    ```

    A saída esperada deve ser semelhante a esta, indicando que o MariaDB está escutando em todas as interfaces de rede:

    ```
    tcp   0   0 0.0.0.0:3306   0.0.0.0:* LISTEN  PID/mariadbd
    ```

---
### Backup do Banco de Dados

O backup do banco de dados está disponível no seguinte link:

[Backup do Banco de Dados](https://drive.google.com/file/d/10L-w8bElzMdkYLiuHNeOXu6ppk4Kcyzg/view?usp=sharing)

### Scripts para OpenVPNServer

Para a máquina OpenVPNServer, são necessários dois scripts:

* **scriptVPN.py:** [Conteúdo do scriptVPN.py](https://docs.google.com/document/d/1oCTJKr9jwqAlwDXoxOC_sRX0LI8ZAN7ZrXmmV9LMEbg/edit?usp=sharing)
* **revogar\_certificado.py:** [Conteúdo do revogar\_certificado.py](https://docs.google.com/document/d/1301rBLTq86ZrVqMKws2cpNJV_7S8xVA621By8Vy5hTA/edit?usp=sharing)

### Configuração de Rede

Para garantir que todas as máquinas se conectem corretamente, configure os arquivos de rede em `/etc/network/interfaces`. O vídeo no final do `README.md` contém um exemplo de como os arquivos devem ser configurados no firewall.

### Criação do Arquivo `gateway.sh`

Crie um arquivo chamado `gateway.sh`.

Edite o arquivo `gateway.sh` conforme necessário para a configuração da sua rede.

Exemplo: 
![](/imgs/image1.png)


---
### Configurando o Serviço `gateway.service` para Inicialização Automática

Para garantir que o script `gateway.sh` seja executado automaticamente na inicialização do sistema, você precisa criar um serviço `systemd`. Siga os passos abaixo:

1.  **Crie e edite o arquivo de serviço:**
    Execute o comando abaixo para criar e abrir o arquivo de configuração do serviço `gateway.service` com o editor `nano`:

    ```bash
    sudo nano /etc/systemd/system/gateway.service
    ```

2.  **Insira o conteúdo no arquivo:**
    Dentro do arquivo `gateway.service`, adicione o seguinte conteúdo:

    ![](/imgs/image2.png)

3.  **Habilite e inicie o serviço (comandos adicionais após a criação do arquivo):**
    Após salvar o arquivo `gateway.service`, você precisará recarregar o daemon do `systemd`, habilitar o serviço para iniciar no boot e, opcionalmente, iniciá-lo imediatamente:

    ```bash
    sudo systemctl daemon-reload
    sudo systemctl enable gateway.service
    sudo systemctl start gateway.service
    ```

---
### Deploy da Aplicação no Tomcat

Para realizar o deploy da sua aplicação, siga os passos abaixo:

1.  **Transferência do arquivo .war:**
    Utilize o **WinSCP** (conforme recomendado anteriormente) para transferir o arquivo `.war` da sua aplicação para o seguinte diretório na máquina **Tomcat**:

    ```
    /opt/tomcat/webapps/
    ```

2.  **Reinicialização do serviço Tomcat:**
    Após a transferência do arquivo `.war`, reinicie o serviço do Tomcat para que a aplicação seja carregada:

    ```bash
    sudo systemctl restart tomcat
    ```

3.  **Verificação de erros (opcional):**
    Caso a aplicação não funcione como esperado, você pode verificar os logs do Tomcat para identificar possíveis erros. Copie o arquivo `catalina.out` para o seu diretório `/home/usuario` e analise as últimas saídas:

    ```bash
    sudo cp /opt/tomcat/logs/catalina.out /home/usuario
    ```
    Em seguida, acesse `/home/usuario` para abrir o arquivo `catalina.out` e verificar os logs.

Com a configuração correta, a aplicação deve funcionar. Você pode ver uma demonstração da aplicação funcionando no seguinte link:

[Demonstração da Aplicação](https://www.youtube.com/watch?v=uwacnnc_lYU)