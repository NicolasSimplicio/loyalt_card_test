-- Criação do Banco de Dados
CREATE DATABASE VanessaDocesDB;
GO

USE VanessaDocesDB;
GO

-- Tabela para armazenar os clientes
CREATE TABLE Clientes (
    id INT IDENTITY(1,1) PRIMARY KEY,   -- ID do cliente (auto incremento)
    nome NVARCHAR(100) NOT NULL,          -- Nome do cliente
    email NVARCHAR(100) NOT NULL UNIQUE  -- Email do cliente (único)
);
GO

-- Tabela para armazenar os cartões de fidelidade
CREATE TABLE CartoesFidelidade (
    id INT IDENTITY(1,1) PRIMARY KEY,    -- ID do cartão de fidelidade (auto incremento)
    cliente_id INT,                       -- Relacionamento com a tabela Clientes
    pontos INT DEFAULT 0,                 -- Pontos acumulados no cartão
    FOREIGN KEY (cliente_id) REFERENCES Clientes(id)  -- Relacionamento com a tabela Clientes
);
GO

-- Tabela para armazenar as compras realizadas
CREATE TABLE Compras (
    id INT IDENTITY(1,1) PRIMARY KEY,    -- ID da compra (auto incremento)
    valor DECIMAL(10,2) NOT NULL,         -- Valor da compra
    data DATETIME NOT NULL DEFAULT GETDATE(), -- Data da compra (com valor padrão de data atual)
    cartaoFidelidade_id INT,              -- Relacionamento com o cartão de fidelidade
    FOREIGN KEY (cartaoFidelidade_id) REFERENCES CartoesFidelidade(id)  -- Relacionamento com a tabela CartoesFidelidade
);
GO
SELECT * FROM Clientes
SELECT * FROM CartoesFidelidade
SELECT * FROM Compras